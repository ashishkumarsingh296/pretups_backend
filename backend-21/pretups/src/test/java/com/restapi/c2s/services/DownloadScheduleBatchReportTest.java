package com.restapi.c2s.services;

import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.restapi.user.service.HeaderColumn;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {DownloadScheduleBatchReport.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class DownloadScheduleBatchReportTest {
    @Autowired
    private DownloadScheduleBatchReport downloadScheduleBatchReport;

    /**
     * Method under test: {@link DownloadScheduleBatchReport#createExcelXFile(String, ArrayList, List)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testCreateExcelXFile() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R005 Unable to load class.
        //   Class: org.apache.xmlbeans.XmlObject
        //   Please check that the class is available on your test runtime classpath.
        //   See https://diff.blue/R005 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        String fileName = "";
        ArrayList<ScheduleBatchMasterVO> listMaster = null;
        List<HeaderColumn> editColumns = null;

        // Act
        String actualCreateExcelXFileResult = this.downloadScheduleBatchReport.createExcelXFile(fileName, listMaster,
                editColumns);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link DownloadScheduleBatchReport#createExcelFile(String, ArrayList, List)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testCreateExcelFile() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IllegalArgumentException: sheetName must not be null
        //       at org.apache.poi.hssf.usermodel.HSSFWorkbook.createSheet(HSSFWorkbook.java:945)
        //       at org.apache.poi.hssf.usermodel.HSSFWorkbook.createSheet(HSSFWorkbook.java:131)
        //       at com.restapi.c2s.services.DownloadScheduleBatchReport.createExcelFile(DownloadScheduleBatchReport.java:289)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<ScheduleBatchMasterVO> listMaster = new ArrayList<>();
        downloadScheduleBatchReport.createExcelFile(null, listMaster, new ArrayList<>());
    }

    /**
     * Method under test: {@link DownloadScheduleBatchReport#createExcelFile(String, ArrayList, List)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testCreateExcelFile2() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IllegalArgumentException: sheetName '' is invalid - character count MUST be greater than or equal to 1 and less than or equal to 31
        //       at org.apache.poi.ss.util.WorkbookUtil.validateSheetName(WorkbookUtil.java:136)
        //       at org.apache.poi.hssf.record.BoundSheetRecord.setSheetname(BoundSheetRecord.java:98)
        //       at org.apache.poi.hssf.model.InternalWorkbook.setSheetName(InternalWorkbook.java:605)
        //       at org.apache.poi.hssf.usermodel.HSSFWorkbook.createSheet(HSSFWorkbook.java:954)
        //       at org.apache.poi.hssf.usermodel.HSSFWorkbook.createSheet(HSSFWorkbook.java:131)
        //       at com.restapi.c2s.services.DownloadScheduleBatchReport.createExcelFile(DownloadScheduleBatchReport.java:289)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<ScheduleBatchMasterVO> listMaster = new ArrayList<>();
        downloadScheduleBatchReport.createExcelFile("", listMaster, new ArrayList<>());
    }

    /**
     * Method under test: {@link DownloadScheduleBatchReport#createExcelFile(String, ArrayList, List)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testCreateExcelFile3() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.DownloadScheduleBatchReport.getMappedColumnValue(DownloadScheduleBatchReport.java:371)
        //       at com.restapi.c2s.services.DownloadScheduleBatchReport.createExcelFile(DownloadScheduleBatchReport.java:315)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<ScheduleBatchMasterVO> listMaster = new ArrayList<>();
        listMaster.add(null);
        downloadScheduleBatchReport.createExcelFile("foo.txt", listMaster, new ArrayList<>());
    }

    /**
     * Method under test: {@link DownloadScheduleBatchReport#createCSVFile(ArrayList, List)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testCreateCSVFile() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.DownloadScheduleBatchReport.getMappedColumnValue(DownloadScheduleBatchReport.java:371)
        //       at com.restapi.c2s.services.DownloadScheduleBatchReport.createCSVFile(DownloadScheduleBatchReport.java:349)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList<ScheduleBatchMasterVO> listMaster = new ArrayList<>();
        listMaster.add(null);
        downloadScheduleBatchReport.createCSVFile(listMaster, new ArrayList<>());
    }
}

