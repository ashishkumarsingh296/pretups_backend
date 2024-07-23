package com.restapi.c2sservices.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchMasterVO;
import com.web.pretups.channel.transfer.web.O2CBatchWithdrawForm;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.clientutils.JerseyUtil;
import com.btsl.util.JUnitConfig;

import java.util.LinkedHashMap;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.junit.Ignore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

public class ReadGenericFileUtilTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFile(HashMap, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFile() throws BTSLBaseException {
        JUnitConfig.init();

        try {
            //   when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }

/*
        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);

     */
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
        HashMap<String, String> fileDetailsMap = new HashMap<>();

        fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, "QSxCLEMsRA0KMSwyLDMsNA0KMSwyLDMsNA0K");

        String pattern = "ddMMyyyyhhmmss";
        String dateInString = new SimpleDateFormat(pattern).format(new Date());

        fileDetailsMap.put(PretupsI.FILE_NAME, "TEST" + dateInString);
        fileDetailsMap.put(PretupsI.SERVICE_KEYWORD, "TEST");
        fileDetailsMap.put(PretupsI.FILE_TYPE1, "csv");


        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFile(fileDetailsMap, 1, errorMap);

        ArrayList batchItemsList = new ArrayList();

        readGenericFileUtil.uploadAndReadGenericFileFOC(fileDetailsMap, 1, errorMap, true, "Externals Txn Mandatory",
                batchItemsList, new ArrayList(), mock(Connection.class));
        readGenericFileUtil.uploadAndReadGenericFileBatchOperatorUserInitiate(fileDetailsMap, 1, 10, errorMap);
        readGenericFileUtil.uploadAndReadGenericFileBatchUserModify(fileDetailsMap, 1, 10, errorMap, new ArrayList());


    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFile(HashMap, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFile2() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);


        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("log4jMMlll", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFile(fileDetailsMap, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFile(HashMap, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFile3() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILETYPE", "FILETYPE");
        fileDetailsMap.put("log4jMMlll", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFile(fileDetailsMap, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFile(HashMap, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFile4() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("SERVICEKEYWORD", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFile(fileDetailsMap, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFile(HashMap, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFile5() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILENAME", "FILENAME");
        fileDetailsMap.put("FILETYPE", "FILETYPE");
        fileDetailsMap.put("log4jMMlll", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFile(fileDetailsMap, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFile(HashMap, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFile6() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILETYPE", "");
        fileDetailsMap.put("log4jMMlll", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFile(fileDetailsMap, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFile(HashMap, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFile7() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
        HashMap<String, String> fileDetailsMap = new HashMap<>();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFile(fileDetailsMap, -1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFile(HashMap, int, ErrorMap)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testUploadAndReadGenericFile8() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateFileName(ReadGenericFileUtil.java:1522)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateFileDetailsMap(ReadGenericFileUtil.java:948)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.uploadAndReadGenericFile(ReadGenericFileUtil.java:111)
        //   See https://diff.blue/R013 to resolve this issue.

        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILENAME", "FILENAME");
        fileDetailsMap.put("FILETYPE", "FILETYPE");
        fileDetailsMap.put("FILEATTACHMENT", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.uploadAndReadGenericFile(fileDetailsMap, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CWithdraw(HashMap, int, ErrorMap, boolean, String, ArrayList)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CWithdraw() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

       /* Workbook  workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        }catch (Exception e){}


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
*/
        HashMap<String, String> fileDetailsMap = new HashMap<>();

        fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, "QSxCLEMsRA0KMSwyLDMsNA0KMSwyLDMsNA0K");

        String pattern = "ddMMyyyyhhmmss";
        String dateInString = new SimpleDateFormat(pattern).format(new Date());

        fileDetailsMap.put(PretupsI.FILE_NAME, "TEST" + dateInString);
        fileDetailsMap.put(PretupsI.SERVICE_KEYWORD, "TEST");
        fileDetailsMap.put(PretupsI.FILE_TYPE1, "csv");


        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        //      thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CWithdraw(fileDetailsMap, 1, errorMap, true,
                "Externals Txn Mandatory", new ArrayList());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CWithdraw(HashMap, int, ErrorMap, boolean, String, ArrayList)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CWithdraw2() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("log4jMMlll", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CWithdraw(fileDetailsMap, 1, errorMap, true,
                "Externals Txn Mandatory", new ArrayList());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CWithdraw(HashMap, int, ErrorMap, boolean, String, ArrayList)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CWithdraw3() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILETYPE", "FILETYPE");
        fileDetailsMap.put("log4jMMlll", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CWithdraw(fileDetailsMap, 1, errorMap, true,
                "Externals Txn Mandatory", new ArrayList());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CWithdraw(HashMap, int, ErrorMap, boolean, String, ArrayList)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CWithdraw4() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("SERVICEKEYWORD", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CWithdraw(fileDetailsMap, 1, errorMap, true,
                "Externals Txn Mandatory", new ArrayList());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CWithdraw(HashMap, int, ErrorMap, boolean, String, ArrayList)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CWithdraw5() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILENAME", "FILENAME");
        fileDetailsMap.put("FILETYPE", "FILETYPE");
        fileDetailsMap.put("log4jMMlll", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CWithdraw(fileDetailsMap, 1, errorMap, true,
                "Externals Txn Mandatory", new ArrayList());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CWithdraw(HashMap, int, ErrorMap, boolean, String, ArrayList)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CWithdraw6() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILETYPE", "");
        fileDetailsMap.put("log4jMMlll", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CWithdraw(fileDetailsMap, 1, errorMap, true,
                "Externals Txn Mandatory", new ArrayList());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CWithdraw(HashMap, int, ErrorMap, boolean, String, ArrayList)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CWithdraw7() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
        HashMap<String, String> fileDetailsMap = new HashMap<>();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CWithdraw(fileDetailsMap, -1, errorMap, true,
                "Externals Txn Mandatory", new ArrayList());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CWithdraw(HashMap, int, ErrorMap, boolean, String, ArrayList)}
     */
    @Test
    // ////@Ignore("TODO: Complete this test")
    public void testUploadAndReadGenericFileO2CWithdraw8() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateFileName(ReadGenericFileUtil.java:1522)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateFileDetailsMap(ReadGenericFileUtil.java:948)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.uploadAndReadGenericFileO2CWithdraw(ReadGenericFileUtil.java:284)
        //   See https://diff.blue/R013 to resolve this issue.


        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);

        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {

        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILENAME", "FILENAME");
        fileDetailsMap.put("FILETYPE", "XLS");
        fileDetailsMap.put("FILEATTACHMENT", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.uploadAndReadGenericFileO2CWithdraw(fileDetailsMap, 1, errorMap, true,
                "Externals Txn Mandatory", new ArrayList());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileFOC(HashMap, int, ErrorMap, boolean, String, ArrayList, ArrayList, Connection)}
     */
    @Test
    public void testUploadAndReadGenericFileFOC() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
        HashMap<String, String> fileDetailsMap = new HashMap<>();


        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        ArrayList batchItemsList = new ArrayList();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileFOC(fileDetailsMap, 1, errorMap, true, "Externals Txn Mandatory",
                batchItemsList, new ArrayList(), mock(Connection.class));
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileFOC(HashMap, int, ErrorMap, boolean, String, ArrayList, ArrayList, Connection)}
     */
    @Test
    public void testUploadAndReadGenericFileFOC2() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();


        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("log4jMMlll", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        ArrayList batchItemsList = new ArrayList();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileFOC(fileDetailsMap, 1, errorMap, true, "Externals Txn Mandatory",
                batchItemsList, new ArrayList(), mock(Connection.class));
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileFOC(HashMap, int, ErrorMap, boolean, String, ArrayList, ArrayList, Connection)}
     */
    @Test
    public void testUploadAndReadGenericFileFOC3() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILETYPE", "FILETYPE");
        fileDetailsMap.put("log4jMMlll", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        ArrayList batchItemsList = new ArrayList();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileFOC(fileDetailsMap, 1, errorMap, true, "Externals Txn Mandatory",
                batchItemsList, new ArrayList(), mock(Connection.class));
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileFOC(HashMap, int, ErrorMap, boolean, String, ArrayList, ArrayList, Connection)}
     */
    @Test
    public void testUploadAndReadGenericFileFOC4() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("SERVICEKEYWORD", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        ArrayList batchItemsList = new ArrayList();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileFOC(fileDetailsMap, 1, errorMap, true, "Externals Txn Mandatory",
                batchItemsList, new ArrayList(), mock(Connection.class));
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileFOC(HashMap, int, ErrorMap, boolean, String, ArrayList, ArrayList, Connection)}
     */
    @Test
    public void testUploadAndReadGenericFileFOC5() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILENAME", "FILENAME");
        fileDetailsMap.put("FILETYPE", "FILETYPE");
        fileDetailsMap.put("log4jMMlll", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        ArrayList batchItemsList = new ArrayList();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileFOC(fileDetailsMap, 1, errorMap, true, "Externals Txn Mandatory",
                batchItemsList, new ArrayList(), mock(Connection.class));
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileFOC(HashMap, int, ErrorMap, boolean, String, ArrayList, ArrayList, Connection)}
     */
    @Test
    public void testUploadAndReadGenericFileFOC6() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILETYPE", "");
        fileDetailsMap.put("log4jMMlll", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        ArrayList batchItemsList = new ArrayList();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileFOC(fileDetailsMap, 1, errorMap, true, "Externals Txn Mandatory",
                batchItemsList, new ArrayList(), mock(Connection.class));
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileFOC(HashMap, int, ErrorMap, boolean, String, ArrayList, ArrayList, Connection)}
     */
    @Test
    public void testUploadAndReadGenericFileFOC7() throws BTSLBaseException {
        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
        HashMap<String, String> fileDetailsMap = new HashMap<>();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        ArrayList batchItemsList = new ArrayList();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileFOC(fileDetailsMap, -1, errorMap, true, "Externals Txn Mandatory",
                batchItemsList, new ArrayList(), mock(Connection.class));
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileFOC(HashMap, int, ErrorMap, boolean, String, ArrayList, ArrayList, Connection)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testUploadAndReadGenericFileFOC8() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateFileName(ReadGenericFileUtil.java:1522)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateFileDetailsMap(ReadGenericFileUtil.java:948)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.uploadAndReadGenericFileFOC(ReadGenericFileUtil.java:451)
        //   See https://diff.blue/R013 to resolve this issue.

        JUnitConfig.init();
        //Mockito.mockStatic(Workbook.class);

        Workbook workBook = mock(Workbook.class);
        Sheet excelsheet = mock(Sheet.class);
        Cell mockCell = mock(Cell.class);
        try {
            when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
        } catch (Exception e) {
        }


        when(workBook.getNumberOfSheets()).thenReturn(2);

        when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);

        when(excelsheet.getRows()).thenReturn(10);
        when(excelsheet.getColumns()).thenReturn(10);
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILENAME", "FILENAME");
        fileDetailsMap.put("FILETYPE", "FILETYPE");
        fileDetailsMap.put("FILEATTACHMENT", "log4jMMlll");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        ArrayList batchItemsList = new ArrayList();
        readGenericFileUtil.uploadAndReadGenericFileFOC(fileDetailsMap, 1, errorMap, true, "Externals Txn Mandatory",
                batchItemsList, new ArrayList(), mock(Connection.class));
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#readUploadedFileBatchOptInit()}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testReadUploadedFileBatchOptInit() throws BTSLBaseException, IOException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.readUploadedFileBatchOptInit(ReadGenericFileUtil.java:1176)
        //   See https://diff.blue/R013 to resolve this issue.

        (new ReadGenericFileUtil()).readUploadedFileBatchOptInit();
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#readExcelForXLSX(String)}
     */
    @Test
    public void testReadExcelForXLSX() throws BTSLBaseException, IOException {
        thrown.expect(BTSLBaseException.class);
        (new ReadGenericFileUtil()).readExcelForXLSX("/directory/foo.txt");
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#readExcelForXLSX(String, int)}
     */
    @Test
    public void testReadExcelForXLSX2() throws BTSLBaseException, IOException {
        thrown.expect(BTSLBaseException.class);
        (new ReadGenericFileUtil()).readExcelForXLSX("/directory/foo.txt", 1);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#readExcelForXLSXBatchOptInit(String, int)}
     */
    @Test
    public void testReadExcelForXLSXBatchOptInit() throws BTSLBaseException, IOException {
        thrown.expect(BTSLBaseException.class);
        (new ReadGenericFileUtil()).readExcelForXLSXBatchOptInit("/directory/foo.txt", 1);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CBatchApproval(HashMap, int, ErrorMap, HashMap, HashMap, String, ArrayList, Map, int, O2CBatchWithdrawForm)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CBatchApproval() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
        HashMap<String, String> fileDetailsMap = new HashMap<>();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        HashMap closedOrderMap = new HashMap();
        HashMap approveRejectMap = new HashMap();
        ArrayList batchItemsList = new ArrayList();
        HashMap<Object, Object> map = new HashMap<>();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CBatchApproval(fileDetailsMap, 1, errorMap, closedOrderMap,
                approveRejectMap, "Externals Txn Mandatory", batchItemsList, map, 1, new O2CBatchWithdrawForm());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CBatchApproval(HashMap, int, ErrorMap, HashMap, HashMap, String, ArrayList, Map, int, O2CBatchWithdrawForm)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CBatchApproval2() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("uploadAndReadGenericFileO2CBatchApproval", "uploadAndReadGenericFileO2CBatchApproval");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        HashMap closedOrderMap = new HashMap();
        HashMap approveRejectMap = new HashMap();
        ArrayList batchItemsList = new ArrayList();
        HashMap<Object, Object> map = new HashMap<>();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CBatchApproval(fileDetailsMap, 1, errorMap, closedOrderMap,
                approveRejectMap, "Externals Txn Mandatory", batchItemsList, map, 1, new O2CBatchWithdrawForm());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CBatchApproval(HashMap, int, ErrorMap, HashMap, HashMap, String, ArrayList, Map, int, O2CBatchWithdrawForm)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CBatchApproval3() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILETYPE", "FILETYPE");
        fileDetailsMap.put("uploadAndReadGenericFileO2CBatchApproval", "uploadAndReadGenericFileO2CBatchApproval");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        HashMap closedOrderMap = new HashMap();
        HashMap approveRejectMap = new HashMap();
        ArrayList batchItemsList = new ArrayList();
        HashMap<Object, Object> map = new HashMap<>();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CBatchApproval(fileDetailsMap, 1, errorMap, closedOrderMap,
                approveRejectMap, "Externals Txn Mandatory", batchItemsList, map, 1, new O2CBatchWithdrawForm());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CBatchApproval(HashMap, int, ErrorMap, HashMap, HashMap, String, ArrayList, Map, int, O2CBatchWithdrawForm)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CBatchApproval4() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("SERVICEKEYWORD", "uploadAndReadGenericFileO2CBatchApproval");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        HashMap closedOrderMap = new HashMap();
        HashMap approveRejectMap = new HashMap();
        ArrayList batchItemsList = new ArrayList();
        HashMap<Object, Object> map = new HashMap<>();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CBatchApproval(fileDetailsMap, 1, errorMap, closedOrderMap,
                approveRejectMap, "Externals Txn Mandatory", batchItemsList, map, 1, new O2CBatchWithdrawForm());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CBatchApproval(HashMap, int, ErrorMap, HashMap, HashMap, String, ArrayList, Map, int, O2CBatchWithdrawForm)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CBatchApproval5() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILENAME", "FILENAME");
        fileDetailsMap.put("FILETYPE", "FILETYPE");
        fileDetailsMap.put("uploadAndReadGenericFileO2CBatchApproval", "uploadAndReadGenericFileO2CBatchApproval");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        HashMap closedOrderMap = new HashMap();
        HashMap approveRejectMap = new HashMap();
        ArrayList batchItemsList = new ArrayList();
        HashMap<Object, Object> map = new HashMap<>();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CBatchApproval(fileDetailsMap, 1, errorMap, closedOrderMap,
                approveRejectMap, "Externals Txn Mandatory", batchItemsList, map, 1, new O2CBatchWithdrawForm());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CBatchApproval(HashMap, int, ErrorMap, HashMap, HashMap, String, ArrayList, Map, int, O2CBatchWithdrawForm)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CBatchApproval6() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILETYPE", "");
        fileDetailsMap.put("uploadAndReadGenericFileO2CBatchApproval", "uploadAndReadGenericFileO2CBatchApproval");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        HashMap closedOrderMap = new HashMap();
        HashMap approveRejectMap = new HashMap();
        ArrayList batchItemsList = new ArrayList();
        HashMap<Object, Object> map = new HashMap<>();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CBatchApproval(fileDetailsMap, 1, errorMap, closedOrderMap,
                approveRejectMap, "Externals Txn Mandatory", batchItemsList, map, 1, new O2CBatchWithdrawForm());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CBatchApproval(HashMap, int, ErrorMap, HashMap, HashMap, String, ArrayList, Map, int, O2CBatchWithdrawForm)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CBatchApproval7() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
        HashMap<String, String> fileDetailsMap = new HashMap<>();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        HashMap closedOrderMap = new HashMap();
        HashMap approveRejectMap = new HashMap();
        ArrayList batchItemsList = new ArrayList();
        HashMap<Object, Object> map = new HashMap<>();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CBatchApproval(fileDetailsMap, -1, errorMap, closedOrderMap,
                approveRejectMap, "Externals Txn Mandatory", batchItemsList, map, 1, new O2CBatchWithdrawForm());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateEachRowFOC(String[], int, ErrorMap, String, int, boolean, String, ArrayList, ArrayList, Connection)}
     */
    @Test
    public void testValidateEachRowFOC() {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        ArrayList batchItemsList = new ArrayList();
        assertTrue(readGenericFileUtil.validateEachRowFOC(new String[]{"File Record"}, 1, errorMap, "Service Keyword", 3,
                true, "Externals Txn Mandatory", batchItemsList, new ArrayList(), mock(Connection.class)));
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateEachRowFOC(String[], int, ErrorMap, String, int, boolean, String, ArrayList, ArrayList, Connection)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateEachRowFOC2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateFOCBatchTrf(ReadGenericFileUtil.java:3805)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateEachRowFOC(ReadGenericFileUtil.java:2694)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        ArrayList batchItemsList = new ArrayList();
        readGenericFileUtil.validateEachRowFOC(new String[]{"File Record"}, 1, errorMap, "DPBATCHTRF", 3, true,
                "Externals Txn Mandatory", batchItemsList, new ArrayList(), mock(Connection.class));
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateDvdBulkFileData(String[], int, ErrorMap)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateDvdBulkFileData() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateDvdBulkFileData(ReadGenericFileUtil.java:2716)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateDvdBulkFileData(new String[]{"File Record"}, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateDvdBulkFileData(String[], int, ErrorMap)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateDvdBulkFileData2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateDvdBulkFileData(ReadGenericFileUtil.java:2716)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateDvdBulkFileData(new String[]{}, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateO2CBatch(String[], int, ErrorMap, int, boolean, String, ArrayList)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateO2CBatch() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateO2CBatch(ReadGenericFileUtil.java:2849)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateO2CBatch(new String[]{"File Record"}, 1, errorMap, 3, true, "Externals Txn Mandatory",
                new ArrayList());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateO2CBatch(String[], int, ErrorMap, int, boolean, String, ArrayList)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateO2CBatch2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateO2CBatch(ReadGenericFileUtil.java:2849)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateO2CBatch(new String[]{}, 1, errorMap, 3, true, "Externals Txn Mandatory",
                new ArrayList());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateBulkPrepaidRc(String[], int, ErrorMap)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateBulkPrepaidRc() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateBulkPrepaidRc(ReadGenericFileUtil.java:3099)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateBulkPrepaidRc(new String[]{"File Record"}, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateBulkPrepaidRc(String[], int, ErrorMap)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateBulkPrepaidRc2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateBulkPrepaidRc(ReadGenericFileUtil.java:3099)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateBulkPrepaidRc(new String[]{}, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateBulkPrepaidRcEVD(String[], int, ErrorMap)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateBulkPrepaidRcEVD() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateBulkPrepaidRcEVD(ReadGenericFileUtil.java:3190)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateBulkPrepaidRcEVD(new String[]{"File Record"}, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateBulkPrepaidRcEVD(String[], int, ErrorMap)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateBulkPrepaidRcEVD2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateBulkPrepaidRcEVD(ReadGenericFileUtil.java:3190)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateBulkPrepaidRcEVD(new String[]{}, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateBulkGiftRc(String[], int, ErrorMap)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateBulkGiftRc() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateBulkGiftRc(ReadGenericFileUtil.java:3277)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateBulkGiftRc(new String[]{"File Record"}, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateBulkGiftRc(String[], int, ErrorMap)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateBulkGiftRc2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateBulkGiftRc(ReadGenericFileUtil.java:3277)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateBulkGiftRc(new String[]{}, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateBulkIntrRc(String[], int, ErrorMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateBulkIntrRc() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateBulkIntrRc(ReadGenericFileUtil.java:3411)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateBulkIntrRc(new String[]{"File Record"}, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateBulkIntrRc(String[], int, ErrorMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateBulkIntrRc2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateBulkIntrRc(ReadGenericFileUtil.java:3411)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateBulkIntrRc(new String[]{}, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateO2CBatchTrf(String[], int, ErrorMap, int, boolean, String, ArrayList)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateO2CBatchTrf() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateO2CBatchTrf(ReadGenericFileUtil.java:3512)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateO2CBatchTrf(new String[]{"File Record"}, 1, errorMap, 3, true,
                "Externals Txn Mandatory", new ArrayList());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateO2CBatchTrf(String[], int, ErrorMap, int, boolean, String, ArrayList)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateO2CBatchTrf2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateO2CBatchTrf(ReadGenericFileUtil.java:3512)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateO2CBatchTrf(new String[]{}, 1, errorMap, 3, true, "Externals Txn Mandatory",
                new ArrayList());
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CBatchWithdrawApproval(Connection, O2CBatchMasterVO, HashMap, int, ErrorMap, LinkedHashMap, LinkedHashMap, String, ArrayList, LinkedHashMap, int, O2CBatchWithdrawForm)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CBatchWithdrawApproval() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
        Connection con = mock(Connection.class);
        O2CBatchMasterVO o2cBatchMasterVO = mock(O2CBatchMasterVO.class);
        HashMap<String, String> fileDetailsMap = new HashMap<>();
        ErrorMap errorMap = mock(ErrorMap.class);
        LinkedHashMap closedOrderMap = new LinkedHashMap();
        LinkedHashMap approveRejectMap = new LinkedHashMap();
        ArrayList batchItemsList = new ArrayList();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CBatchWithdrawApproval (com.btsl.util.JUnitConfig.getConnection(), o2cBatchMasterVO, fileDetailsMap, 1,
                errorMap, closedOrderMap, approveRejectMap, "Externals Txn Mandatory", batchItemsList, new LinkedHashMap(), 1,
                mock(O2CBatchWithdrawForm.class));
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CBatchWithdrawApproval(Connection, O2CBatchMasterVO, HashMap, int, ErrorMap, LinkedHashMap, LinkedHashMap, String, ArrayList, LinkedHashMap, int, O2CBatchWithdrawForm)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CBatchWithdrawApproval2() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
        Connection con = mock(Connection.class);

        FOCBatchItemsVO batchItemsVO = new FOCBatchItemsVO();
        batchItemsVO.setBatchDetailId("42");
        batchItemsVO.setBatchId("42");
        batchItemsVO.setBonusType("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setBonusTypeList(new ArrayList());
        batchItemsVO.setCancelledBy("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO
                .setCancelledOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        batchItemsVO.setCategoryCode("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setCategoryName("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setCommWalletType("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setCommissionProfileDetailId("42");
        batchItemsVO.setCommissionProfileSetId("42");
        batchItemsVO.setCommissionProfileVer("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setCommissionRate(10.0d);
        batchItemsVO.setCommissionType("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setCommissionValue(42L);
        batchItemsVO.setDualCommissionType("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setError("An error occurred");
        batchItemsVO.setExtCode("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setExtTXNDate("2020-03-01");
        batchItemsVO.setExtTXNNumber("42");
        batchItemsVO.setExtTxnDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        batchItemsVO.setExtTxnDateStr("2020-03-01");
        batchItemsVO.setExtTxnNo("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setExternalCode("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setFirstApprovedBy("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO
                .setFirstApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        batchItemsVO.setFirstApproverName("Jane");
        batchItemsVO.setFirstApproverRemarks("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setGradeCode("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setGradeName("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setInitiatedBy("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO
                .setInitiatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        batchItemsVO.setInitiaterName("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setInitiatorRemarks("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setLoginID("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        batchItemsVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        batchItemsVO.setMsisdn("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setNewStatus("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setOwnerMSISDN("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setOwnerName("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setPointAction("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setPostBalance("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setPreBalance("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setRcrdStatus("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setRecordNumber(10);
        batchItemsVO.setReferenceNo("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setRequestedQuantity(1L);
        batchItemsVO.setSecondApprovedBy("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO
                .setSecondApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        batchItemsVO.setSecondApproverName("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setSecondApproverRemarks("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setStatus("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setTax1Rate(10.0d);
        batchItemsVO.setTax1Type("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setTax1Value(42L);
        batchItemsVO.setTax2Rate(10.0d);
        batchItemsVO.setTax2Type("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setTax2Value(42L);
        batchItemsVO.setTax3Rate(10.0d);
        batchItemsVO.setTax3Type("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setTax3Value(42L);
        batchItemsVO.setThirdApprovedBy("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO
                .setThirdApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        batchItemsVO.setThirdApproverRemarks("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO
                .setTransferDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        batchItemsVO.setTransferDateStr("2020-03-01");
        batchItemsVO.setTransferMrp(1L);
        batchItemsVO.setTxnProfile("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setUserGradeCode("uploadAndReadGenericFileO2CBatchApproval");
        batchItemsVO.setUserId("42");
        batchItemsVO.setUserName("janedoe");
        batchItemsVO.setWalletCode("uploadAndReadGenericFileO2CBatchApproval");

        O2CBatchMasterVO o2cBatchMasterVO = new O2CBatchMasterVO();
        o2cBatchMasterVO
                .setBatchDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        o2cBatchMasterVO.setBatchDateStr("2020-03-01");
        o2cBatchMasterVO.setBatchFileName("foo.txt");
        o2cBatchMasterVO.setBatchId("42");
        o2cBatchMasterVO.setBatchName("uploadAndReadGenericFileO2CBatchApproval");
        o2cBatchMasterVO.setBatchTotalRecord(1);
        o2cBatchMasterVO.setCategoryCode("uploadAndReadGenericFileO2CBatchApproval");
        o2cBatchMasterVO.setClosedRecords(1);
        o2cBatchMasterVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        o2cBatchMasterVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        o2cBatchMasterVO.setDefaultLang("uploadAndReadGenericFileO2CBatchApproval");
        o2cBatchMasterVO.setDomainCode("uploadAndReadGenericFileO2CBatchApproval");
        o2cBatchMasterVO.setDomainCodeDesc("uploadAndReadGenericFileO2CBatchApproval");
        o2cBatchMasterVO.setFocBatchItemsVO(batchItemsVO);
        o2cBatchMasterVO.setGeographyList(new ArrayList());
        o2cBatchMasterVO.setLevel1ApprovedRecords(1);
        o2cBatchMasterVO.setLevel2ApprovedRecords(1);
        o2cBatchMasterVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        o2cBatchMasterVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        o2cBatchMasterVO.setNetworkCode("uploadAndReadGenericFileO2CBatchApproval");
        o2cBatchMasterVO.setNetworkCodeFor("uploadAndReadGenericFileO2CBatchApproval");
        o2cBatchMasterVO.setNewRecords(1);
        o2cBatchMasterVO.setProductCode("uploadAndReadGenericFileO2CBatchApproval");
        o2cBatchMasterVO.setProductCodeDesc("uploadAndReadGenericFileO2CBatchApproval");
        o2cBatchMasterVO.setProductMrp(1L);
        o2cBatchMasterVO.setProductMrpStr("uploadAndReadGenericFileO2CBatchApproval");
        o2cBatchMasterVO.setProductName("uploadAndReadGenericFileO2CBatchApproval");
        o2cBatchMasterVO.setProductShortName("uploadAndReadGenericFileO2CBatchApproval");
        o2cBatchMasterVO.setProductType("uploadAndReadGenericFileO2CBatchApproval");
        o2cBatchMasterVO.setRejectedRecords(1);
        o2cBatchMasterVO.setSecondLang("uploadAndReadGenericFileO2CBatchApproval");
        o2cBatchMasterVO.setStatus("uploadAndReadGenericFileO2CBatchApproval");
        o2cBatchMasterVO.setStatusDesc("uploadAndReadGenericFileO2CBatchApproval");
        HashMap<String, String> fileDetailsMap = new HashMap<>();
        ErrorMap errorMap = mock(ErrorMap.class);
        LinkedHashMap closedOrderMap = new LinkedHashMap();
        LinkedHashMap approveRejectMap = new LinkedHashMap();
        ArrayList batchItemsList = new ArrayList();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CBatchWithdrawApproval (com.btsl.util.JUnitConfig.getConnection(), o2cBatchMasterVO, fileDetailsMap, 1,
                errorMap, closedOrderMap, approveRejectMap, "Externals Txn Mandatory", batchItemsList, new LinkedHashMap(), 1,
                mock(O2CBatchWithdrawForm.class));
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileO2CBatchWithdrawApproval(Connection, O2CBatchMasterVO, HashMap, int, ErrorMap, LinkedHashMap, LinkedHashMap, String, ArrayList, LinkedHashMap, int, O2CBatchWithdrawForm)}
     */
    @Test
    public void testUploadAndReadGenericFileO2CBatchWithdrawApproval3() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
        Connection con = mock(Connection.class);
        O2CBatchMasterVO o2cBatchMasterVO = mock(O2CBatchMasterVO.class);
        HashMap<String, String> fileDetailsMap = new HashMap<>();
        ErrorMap errorMap = mock(ErrorMap.class);
        LinkedHashMap closedOrderMap = new LinkedHashMap();
        LinkedHashMap approveRejectMap = new LinkedHashMap();
        ArrayList batchItemsList = new ArrayList();
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileO2CBatchWithdrawApproval (com.btsl.util.JUnitConfig.getConnection(), o2cBatchMasterVO, fileDetailsMap, -1,
                errorMap, closedOrderMap, approveRejectMap, "Externals Txn Mandatory", batchItemsList, new LinkedHashMap(), 1,
                mock(O2CBatchWithdrawForm.class));
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#deleteUploadedFile(String)}
     */
    @Test
    public void testDeleteUploadedFile() {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
        readGenericFileUtil.deleteUploadedFile("Dr Jane Doe");
        assertTrue(readGenericFileUtil.externalTxnNumber.isEmpty());
        assertEquals(0, readGenericFileUtil.rowNum);
        assertFalse(readGenericFileUtil.fileExist);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#deleteUploadedFile(String)}
     */
    @Test
    public void testDeleteUploadedFile2() {
        com.btsl.util.JUnitConfig.init();
        //   Diffblue AI was unable to find a test

        (new ReadGenericFileUtil()).deleteUploadedFile(".");
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileBatchOperatorUserInitiate(HashMap, int, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFileBatchOperatorUserInitiate() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
        HashMap<String, String> fileDetailsMap = new HashMap<>();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileBatchOperatorUserInitiate(fileDetailsMap, 1, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileBatchOperatorUserInitiate(HashMap, int, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFileBatchOperatorUserInitiate2() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("uploadAndReadGenericFileBatchUserInitiate", "uploadAndReadGenericFileBatchUserInitiate");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileBatchOperatorUserInitiate(fileDetailsMap, 1, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileBatchOperatorUserInitiate(HashMap, int, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFileBatchOperatorUserInitiate3() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILETYPE", "uploadAndReadGenericFileBatchUserInitiate");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileBatchOperatorUserInitiate(fileDetailsMap, 1, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileBatchOperatorUserInitiate(HashMap, int, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFileBatchOperatorUserInitiate4() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("SERVICEKEYWORD", "uploadAndReadGenericFileBatchUserInitiate");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileBatchOperatorUserInitiate(fileDetailsMap, 1, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileBatchOperatorUserInitiate(HashMap, int, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFileBatchOperatorUserInitiate5() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
        HashMap<String, String> fileDetailsMap = new HashMap<>();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileBatchOperatorUserInitiate(fileDetailsMap, -1, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileBatchOperatorUserInitiate(HashMap, int, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFileBatchOperatorUserInitiate6() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILETYPE", "");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileBatchOperatorUserInitiate(fileDetailsMap, 1, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileForUserMovement(HashMap, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFileForUserMovement() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
        HashMap<String, String> fileDetailsMap = new HashMap<>();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileForUserMovement(fileDetailsMap, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileForUserMovement(HashMap, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFileForUserMovement2() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("uploadAndReadGenericFileForUserMovement", "uploadAndReadGenericFileForUserMovement");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileForUserMovement(fileDetailsMap, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileForUserMovement(HashMap, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFileForUserMovement3() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILETYPE", "FILETYPE");
        fileDetailsMap.put("uploadAndReadGenericFileForUserMovement", "uploadAndReadGenericFileForUserMovement");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileForUserMovement(fileDetailsMap, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileForUserMovement(HashMap, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFileForUserMovement4() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("SERVICEKEYWORD", "uploadAndReadGenericFileForUserMovement");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileForUserMovement(fileDetailsMap, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileForUserMovement(HashMap, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFileForUserMovement5() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILENAME", "FILENAME");
        fileDetailsMap.put("FILETYPE", "FILETYPE");
        fileDetailsMap.put("uploadAndReadGenericFileForUserMovement", "uploadAndReadGenericFileForUserMovement");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileForUserMovement(fileDetailsMap, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileForUserMovement(HashMap, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFileForUserMovement6() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILETYPE", "");
        fileDetailsMap.put("uploadAndReadGenericFileForUserMovement", "uploadAndReadGenericFileForUserMovement");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileForUserMovement(fileDetailsMap, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileForUserMovement(HashMap, int, ErrorMap)}
     */
    @Test
    public void testUploadAndReadGenericFileForUserMovement7() throws BTSLBaseException {
        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();
        HashMap<String, String> fileDetailsMap = new HashMap<>();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        thrown.expect(BTSLBaseException.class);
        readGenericFileUtil.uploadAndReadGenericFileForUserMovement(fileDetailsMap, -1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#uploadAndReadGenericFileForUserMovement(HashMap, int, ErrorMap)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testUploadAndReadGenericFileForUserMovement8() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateFileName(ReadGenericFileUtil.java:1530)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateFileDetailsMap(ReadGenericFileUtil.java:944)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.uploadAndReadGenericFileForUserMovement(ReadGenericFileUtil.java:5072)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILENAME", "FILENAME");
        fileDetailsMap.put("FILETYPE", "FILETYPE");
        fileDetailsMap.put("FILEATTACHMENT", "uploadAndReadGenericFileForUserMovement");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.uploadAndReadGenericFileForUserMovement(fileDetailsMap, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateBulkModifyChannelUser(String[], int, ErrorMap)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateBulkModifyChannelUser() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateBulkModifyChannelUser(ReadGenericFileUtil.java:5164)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateBulkModifyChannelUser(new String[]{"File Record"}, 1, errorMap);
    }

    /**
     * Method under test: {@link ReadGenericFileUtil#validateBulkModifyChannelUser(String[], int, ErrorMap)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateBulkModifyChannelUser2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2sservices.service.ReadGenericFileUtil.validateBulkModifyChannelUser(ReadGenericFileUtil.java:5164)
        //   See https://diff.blue/R013 to resolve this issue.

        ReadGenericFileUtil readGenericFileUtil = new ReadGenericFileUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        readGenericFileUtil.validateBulkModifyChannelUser(new String[]{}, 1, errorMap);
    }
}

