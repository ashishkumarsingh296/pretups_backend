package com.restapi.networkadmin.utils;

import static org.junit.Assert.assertNull;

import com.btsl.pretups.common.BTSLMessageResources;

import java.util.HashMap;
import java.util.Locale;

import com.btsl.util.MessageResources;
import org.apache.struts.util.PropertyMessageResourcesFactory;
import org.junit.Test;

public class BatchTransferRuleExcelRWTest {
    /**
     * Method under test: {@link BatchTransferRuleExcelRW#writeExcel(String, HashMap, MessageResources, Locale, String, String, String)}
     */
    @Test
    public void testWriteExcel() {
        BatchTransferRuleExcelRW batchTransferRuleExcelRW = new BatchTransferRuleExcelRW();
        HashMap p_hashMap = new HashMap();
        BTSLMessageResources messages = new BTSLMessageResources(new PropertyMessageResourcesFactory(), "Config");

        batchTransferRuleExcelRW.writeExcel("P excel ID", p_hashMap, messages, Locale.getDefault(), "Promotion Level",
                "foo.txt", "2020-03-01");
        assertNull(batchTransferRuleExcelRW.readExcel("P excel ID", "foo.txt"));
    }

    /**
     * Method under test: {@link BatchTransferRuleExcelRW#writeExcel(String, HashMap, MessageResources, Locale, String, String, String)}
     */
    @Test
    public void testWriteExcel2() {
        BatchTransferRuleExcelRW batchTransferRuleExcelRW = new BatchTransferRuleExcelRW();
        HashMap p_hashMap = new HashMap();
        BTSLMessageResources messages = new BTSLMessageResources(new PropertyMessageResourcesFactory(), "Config");

        batchTransferRuleExcelRW.writeExcel("writeExcel", p_hashMap, messages, Locale.getDefault(), "Promotion Level",
                "foo.txt", "2020-03-01");
        assertNull(batchTransferRuleExcelRW.readExcel("P excel ID", "foo.txt"));
    }

    /**
     * Method under test: {@link BatchTransferRuleExcelRW#writeExcel(String, HashMap, MessageResources, Locale, String, String, String)}
     */
    @Test
    public void testWriteExcel3() {
        BatchTransferRuleExcelRW batchTransferRuleExcelRW = new BatchTransferRuleExcelRW();
        HashMap p_hashMap = new HashMap();
        BTSLMessageResources messages = new BTSLMessageResources(new PropertyMessageResourcesFactory(), "Config");

        batchTransferRuleExcelRW.writeExcel("P excel ID", p_hashMap, messages, Locale.getDefault(), "Promotion Level",
                null, "2020-03-01");
        assertNull(batchTransferRuleExcelRW.readExcel("P excel ID", "foo.txt"));
    }

    /**
     * Method under test: {@link BatchTransferRuleExcelRW#readExcel(String, String)}
     */
    @Test
    public void testReadExcel() {
        assertNull((new BatchTransferRuleExcelRW()).readExcel("P excel ID", "foo.txt"));
        assertNull((new BatchTransferRuleExcelRW()).readExcel("readExcel", "foo.txt"));
        assertNull((new BatchTransferRuleExcelRW()).readExcel("P excel ID", null));
    }

    /**
     * Method under test: {@link BatchTransferRuleExcelRW#readMultipleExcelSheet(String, String, boolean, int, HashMap)}
     */
    @Test
    public void testReadMultipleExcelSheet() {
        BatchTransferRuleExcelRW batchTransferRuleExcelRW = new BatchTransferRuleExcelRW();
        assertNull(batchTransferRuleExcelRW.readMultipleExcelSheet("P excel ID", "foo.txt", true, 2, new HashMap<>()));
    }

    /**
     * Method under test: {@link BatchTransferRuleExcelRW#readMultipleExcelSheet(String, String, boolean, int, HashMap)}
     */
    @Test
    public void testReadMultipleExcelSheet2() {
        BatchTransferRuleExcelRW batchTransferRuleExcelRW = new BatchTransferRuleExcelRW();
        assertNull(batchTransferRuleExcelRW.readMultipleExcelSheet("readMultipleExcelSheet", "foo.txt", true, 2,
                new HashMap<>()));
    }

    /**
     * Method under test: {@link BatchTransferRuleExcelRW#readMultipleExcelSheet(String, String, boolean, int, HashMap)}
     */
    @Test
    public void testReadMultipleExcelSheet3() {
        BatchTransferRuleExcelRW batchTransferRuleExcelRW = new BatchTransferRuleExcelRW();
        assertNull(batchTransferRuleExcelRW.readMultipleExcelSheet("P excel ID", null, true, 2, new HashMap<>()));
    }
}

