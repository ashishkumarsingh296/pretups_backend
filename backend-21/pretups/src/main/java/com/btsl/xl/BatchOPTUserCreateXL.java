package com.btsl.xl;

/*
 * @# BatchOPTUserCreateXL.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Shishupal Singh March 21, 2007 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2007 Bharti Telesoft Ltd.
 * This class use for read write in xls file for batch operator user creation.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import com.btsl.util.MessageResources;

import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.user.businesslogic.BatchOPTUserVO;
import com.btsl.user.businesslogic.UserGeographiesVO;

import jxl.Cell;
import jxl.Sheet;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * @author shishupal.singh
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class BatchOPTUserCreateXL {
    private final Log _log = LogFactory.getLog(this.getClass().getName());
    private final int COLUMN_MARGE = 10;
    private WritableCellFeatures cellFeatures = new WritableCellFeatures();
    private WritableFont times12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
    private WritableCellFormat times12format = new WritableCellFormat(times12font);
    private WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
    private WritableCellFormat times16format = new WritableCellFormat(times16font);
    private MessageResources p_messages = null;
    private Locale p_locale = null;



    /**
     * readExcel
     * 
     * @param p_excelID
     *            String
     * @param p_fileName
     *            String
     * @return strArr String[][]
     * @throws Exception
     * @author shishupal.singh
     * @throws IOException 
     * @throws BiffException 
     */

    public String[][] readExcel(String p_excelID, String p_fileName) throws BiffException, IOException {
        if (_log.isDebugEnabled())
            _log.debug("readExcel", " p_excelID: " + p_excelID + " p_fileName: " + p_fileName);
        final String METHOD_NAME = "readExcel";
        String strArr[][] = null;
        Workbook workbook = null;
        Sheet excelsheet = null;
        try {
            workbook = Workbook.getWorkbook(new File(p_fileName));
            excelsheet = workbook.getSheet(0);
            int noOfRows = excelsheet.getRows();
            int noOfcols = excelsheet.getColumns();
            strArr = new String[noOfRows][noOfcols];
            Cell cell = null;
            String content = null;
            String key = null;
            int[] indexMapArray = new int[noOfcols];
            String indexStr = null;
            for (int col = 0; col < noOfcols; col++) {
                indexStr = null;
                key = ExcelFileConstants.getReadProperty(p_excelID, String.valueOf(col));
                if (key == null)
                    key = String.valueOf(col);
                indexStr = ExcelFileConstants.getReadProperty(p_excelID, String.valueOf(col));
                if (indexStr == null)
                    indexStr = String.valueOf(col);
                indexMapArray[col] = Integer.parseInt(indexStr);
                strArr[0][indexMapArray[col]] = key;
            }
            for (int row = 1; row < noOfRows; row++) {
                for (int col = 0; col < noOfcols; col++) {
                    cell = excelsheet.getCell(col, row);
                    content = cell.getContents();
                    strArr[row][indexMapArray[col]] = content.replaceAll("\n", " ").replaceAll("\r", " ");
                    ;
                }
            }

            return strArr;
        } catch (BiffException | IOException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("readExcel", " Exception e: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (workbook != null)
                    workbook.close();
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            workbook = null;
            excelsheet = null;
            if (_log.isDebugEnabled())
                _log.debug("readExcel", " Exiting strArr: " + strArr);
        }
    }

    /**
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     * @author shishupal.singh
     */
    private int writeUserPrefix(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException{
        if (_log.isDebugEnabled())
            _log.debug("writeUserPrifix", " p_hashMap size=" + p_hashMap.size() + ", p_locale: " + p_locale + ", col=" + col + ", row=" + row);
        final String METHOD_NAME = "writeUserPrefix";
        try {
            String keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.prefixheading");

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.prefixheading.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.prefixcode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.prefixname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USER_PREFIX_LIST);
            ListValueVO listValueVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserPrifix", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserPrifix", " Exception e: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Method writeGeographyListing
     * This method writes the Geography Details containing zone,area,sub area
     * etc. [ N level geographies can exists ]
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     * @author shishupal singh
     */
    private int writeGeographyListing(WritableSheet worksheet2, int col, int row, HashMap p_hashMap,CategoryVO categoryVO) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeGeographyListing", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        final String METHOD_NAME = "writeGeographyListing";
        try {
        	String keyName=null;
        	if((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(categoryVO.getCategoryCode()))||(TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(categoryVO.getCategoryCode())))
        	{
        		keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.networklist");
        	}
        	else
        	{
        		keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.network");
        	}	
        	Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            if((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(categoryVO.getCategoryCode()))||(TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(categoryVO.getCategoryCode())))
        	{
            	keyName = p_messages.getMessage(p_locale,"user.initiatebatchoperatoruser.mastersheet.networklist.note");
        	}
            else
            {
            	keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.network.note");
            }
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            if((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(categoryVO.getCategoryCode()))||(TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(categoryVO.getCategoryCode())))
        	{
            	keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.networklist.networkcode");
        	}
            else
            {
            	keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.networkcode");
            }
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            if(!((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(categoryVO.getCategoryCode()))||(TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(categoryVO.getCategoryCode()))))
        	{
            	keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.networkname");
            	label = new Label(col + 1, row, keyName, times16format);
                worksheet2.addCell(label);
        	}
            
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_GEOGRAPHY_LIST);
            if (list != null && list.size() > 0) {
                UserGeographiesVO userGeographiesVO = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    userGeographiesVO = (UserGeographiesVO) list.get(i);
                    label = new Label(col, row, userGeographiesVO.getGraphDomainCode());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, userGeographiesVO.getGraphDomainName());
                    worksheet2.addCell(label);
                    row++;
                    col = 0;
                }
            }

            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGeographyListing", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGeographyListing", " Exception e: " + e.getMessage());
            throw e;
        }

    }



    /**
     * method writeStausType
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     * @author shishupal.singh
     */
    private int writeStausType(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeStausType", " p_hashMap size=" + p_hashMap.size() + ", p_locale: " + p_locale + ", col=" + col + ", row=" + row);
        final String METHOD_NAME = "writeStausType";
        try {
            String keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.status");

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.status.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.statustype");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.statusname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_STATUS_LIST);
            ListValueVO listValueVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeStausType", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeStausType", " Exception e: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Method writeRolesList
     * This method writes the Geography Details containing zone,area,sub area
     * etc. [ N level geographies can exists ]
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     * @author Shishupal Singh
     */
    private int writeRolesList(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeRolesList", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        final String METHOD_NAME = "writeRolesList";
        try {
            String keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assignroles");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assignroles.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            // Logic for generating headings
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.rolesgroupname");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assignrolestype");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assignrolesname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            HashMap hm = (HashMap) p_hashMap.get(PretupsI.BATCH_OPT_USR_ASSIGN_ROLES);
            UserRolesVO userRolesVO = null;

            if (hm != null && hm.size() > 0) {
                String key = null;
                Set set = hm.keySet();
                Iterator itr = set.iterator();
                ArrayList rolesList = null;
                while (itr.hasNext()) {
                    key = (String) itr.next();
                    col = 0;
                    label = new Label(col, row, key + ":");
                    worksheet2.addCell(label);
                    row++;
                    rolesList = (ArrayList) hm.get(key);
                    Iterator rolesListItr = rolesList.iterator();
                    while (rolesListItr.hasNext()) {
                        userRolesVO = (UserRolesVO) rolesListItr.next();
                        if (userRolesVO.getGroupRole().equalsIgnoreCase("Y")) {
                            row--;
                            break;
                        }
                        col = 0;
                        label = new Label(col + 1, row, userRolesVO.getRoleCode());
                        worksheet2.addCell(label);
                        label = new Label(col + 2, row, userRolesVO.getRoleName());
                        worksheet2.addCell(label);
                        row++;
                    }
                    if (userRolesVO.getGroupRole().equalsIgnoreCase("Y")) {
                        label = new Label(0, row, "");
                        worksheet2.addCell(label);
                    }
                    if (_log.isDebugEnabled())
                        _log.debug("writeRolesList", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale);
                }
            }

            row++;
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assigngrouproles");
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assigngrouproles.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            // Logic for generating headings
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.rolesgroupname");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assigngrouproletype");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assigngrouprolename");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            if (hm != null && hm.size() > 0) {
                String key = null;
                Set set = hm.keySet();
                Iterator itr = set.iterator();
                ArrayList rolesList = null;
                while (itr.hasNext()) {
                    key = (String) itr.next();
                    col = 0;
                    label = new Label(col, row, key + ":");
                    worksheet2.addCell(label);
                    row++;
                    rolesList = (ArrayList) hm.get(key);
                    Iterator rolesListItr = rolesList.iterator();
                    while (rolesListItr.hasNext()) {
                        userRolesVO = (UserRolesVO) rolesListItr.next();
                        if (userRolesVO.getGroupRole().equalsIgnoreCase("N")) {
                            row--;
                            break;
                        }
                        col = 0;
                        label = new Label(col + 1, row, userRolesVO.getRoleCode());
                        worksheet2.addCell(label);
                        label = new Label(col + 2, row, userRolesVO.getRoleName());
                        worksheet2.addCell(label);
                        row++;
                    }
                    if (userRolesVO.getGroupRole().equalsIgnoreCase("N")) {
                        label = new Label(0, row, "");
                        worksheet2.addCell(label);
                    }
                    if (_log.isDebugEnabled())
                        _log.debug("writeRolesList", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale);
                }
            }

            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeRolesList", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeRolesList", " Exception e: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Method writeDivisionDepartmentListing
     * This method writes the Division & Department Details.
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     * @author Shishupal Singh
     */
    private int writeDivisionDepartmentListing(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeDivisionDepartmentListing", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        final String METHOD_NAME = "writeDivisionDepartmentListing";
        try {
            String keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.divdept");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.divdept.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.divisiontype");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.divisionname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.departmenttype");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.departmentname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_DIVDEPT_LIST);
            BatchOPTUserVO batchOPTUserVO = null;
            ArrayList divList = new ArrayList();
            if (list != null && list.size() > 0) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    batchOPTUserVO = (BatchOPTUserVO) list.get(i);
                    if (batchOPTUserVO.getDivdeptID().equals(batchOPTUserVO.getParentID())) {
                        divList.add(batchOPTUserVO);
                    }
                }
            }

            row++;
            col = 0;
            BatchOPTUserVO batchOPTUserVO1 = null;
            if (divList != null && divList.size() > 0) {
                for (int k = 0, l = divList.size(); k < l; k++) {
                    batchOPTUserVO = (BatchOPTUserVO) divList.get(k);
                    label = new Label(col, row, batchOPTUserVO.getDivdeptID());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, batchOPTUserVO.getDivdeptName());
                    worksheet2.addCell(label);
                    row++;
                    for (int i = 0, j = list.size(); i < j; i++) {
                        batchOPTUserVO1 = (BatchOPTUserVO) list.get(i);
                        if (batchOPTUserVO.getDivdeptID().equals(batchOPTUserVO1.getParentID()) && !batchOPTUserVO1.getDivdeptID().equals(batchOPTUserVO1.getParentID())) {
                            label = new Label(col + 2, row, batchOPTUserVO1.getDivdeptID());
                            worksheet2.addCell(label);
                            label = new Label(col + 3, row, batchOPTUserVO1.getDivdeptName());
                            worksheet2.addCell(label);
                            row++;
                        }
                    }
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeDivisionDepartmentListing", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeDivisionDepartmentListing", " Exception e: " + e.getMessage());
            throw e;
        }

    }

    /**
     * Method writeDomainListing
     * This method writes the Domain Details containing distributor,dealer etc.
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     * @author shishupal singh
     */
    private int writeDomainListing(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeDomainListing", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        final String METHOD_NAME = "writeDomainListing";
        try {
            String keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.domain");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.domain.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            // Logic for generating headings
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_DOMAIN_LIST);

            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.domaincode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.domainname");
            label = new Label(col + 1, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            if (list != null && list.size() > 0) {
                DomainVO domainVO = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    domainVO = (DomainVO) list.get(i);
                    label = new Label(col, row, domainVO.getDomainCodeforDomain());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, domainVO.getDomainName());
                    worksheet2.addCell(label);
                    row++;
                    col = 0;
                }
            }

            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeDomainListing", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeDomainListing", " Exception e: " + e.getMessage());
            throw e;
        }

    }

    /**
     * Method writeProductListing
     * This method writes the Domain Details containing distributor,dealer etc.
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     * @author shishupal singh
     */
    private int writeProductListing(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeProductListing", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        final String METHOD_NAME = "writeProductListing";
        try {
            String keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.product");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.product.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.productcode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.productname");
            label = new Label(col + 1, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            // Logic for generating headings
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_PRODUCT_LIST);

            ListValueVO listValueVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }

            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeProductListing", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeProductListing", " Exception e: " + e.getMessage());
            throw e;
        }

    }
    
    /**
     * Method writeVoucherListing
     * This method writes the voucher Details 
     * 
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     * @author 
     */
    private int writeVoucherListing(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeVoucherListing", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        final String METHOD_NAME = "writeVoucherListing";
        try {
        	String keyName=null;
        	
			keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.voucher");
        	
        	Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
           
			keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.voucher.note");
            
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchertype");
            
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            
            	keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchername");
            	label = new Label(col + 1, row, keyName, times16format);
                worksheet2.addCell(label);
        	
            
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_VOUCHERTYPE_LIST);
            if (list != null && list.size() > 0) {
                ListValueVO listValueVO = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                    col = 0;
                }
            }

            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeVoucherListing", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeVoucherListing", " Exception e: " + e.getMessage());
            throw e;
        }

    }
    
    private int writeVoucherSegmentListing(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
    	final String methodName = "writeVoucherSegmentListing";
    	if (_log.isDebugEnabled())
            _log.debug(methodName, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        try {
        	String keyName=null;
        	
			keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.voucher.segment");
        	
        	Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
			keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.voucher.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchersegment");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
        	keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchersegment.name");
        	label = new Label(col + 1, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_VOUCHERSEGMENT_LIST);
            if (list != null && list.size() > 0) {
                ListValueVO listValueVO = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                    col = 0;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw e;
        }

    }
    
    public void BatchOperatorUserInitiateWriteExcel(String p_excelID, CategoryVO p_categoryVO, HashMap p_hashMap, Locale locale, String p_fileName) throws IOException, RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeExcel", " p_excelID: " + p_excelID + ", p_hashMap size:" + p_hashMap.size() + ", p_locale: " + locale + ", p_fileName: " + p_fileName);
        final String METHOD_NAME = "writeExcel";
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int col = 0;
        int row = 0;

        try {

            workbook = Workbook.createWorkbook(new File(p_fileName));
            worksheet1 = workbook.createSheet("Template Sheet", 0);
            worksheet2 = workbook.createSheet("Master Sheet", 1);
            p_locale = locale;
            Label label = null;
            String keyName = null;

            // times16format.setWrap(true);
            // times16format.setAlignment(Alignment.CENTRE);
			String arr[] = {(String) p_categoryVO.getCategoryName()};
            keyName = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MASTER_SHEET_HEADING, arr);
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 5, row);
            worksheet2.addCell(label);
            row++;
            /* 1 */row++;
            col = 0;
            row = this.BatchOperatorUserInitiateWriteUserPrefix(worksheet2, col, row, p_hashMap);
            /* 2 */row++;
            col = 0;
            row = this.BatchOperatorUserInitiateWriteStausType(worksheet2, col, row, p_hashMap);
            /* 3 */row++;
            col = 0;
            row = this.BatchOperatorUserInitiateWriteDivisionDepartmentListing(worksheet2, col, row, p_hashMap);

            /* 5 */row++;
            col = 0;
            row = this.BatchOperatorUserInitiateWriteGeographyListing(worksheet2, col, row, p_hashMap,p_categoryVO);
            /* 6 */row++;
            col = 0;
            row = this.BatchOperatorUserInitiateWriteRolesList(worksheet2, col, row, p_hashMap);
            /* 7 */if (p_categoryVO.getDomainAllowed().equalsIgnoreCase(PretupsI.YES) && PretupsI.DOMAINS_ASSIGNED.equals(p_categoryVO.getFixedDomains())) {
                row++;
                col = 0;
                row = this.BatchOperatorUserInitiateWriteDomainListing(worksheet2, col, row, p_hashMap);
            }
            /* 8 */if (p_categoryVO.getProductTypeAllowed().equalsIgnoreCase(PretupsI.YES)) {
                row++;
                col = 0;
                row = this.BatchOperatorUserInitiateWriteProductListing(worksheet2, col, row, p_hashMap);
            }
            /* 9 */ 
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
            {
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_VOUCHERTYPE_LIST);
            	if (list != null && list.size() > 0) {
            	row++;
            	col = 0;
            	row = this.BatchOperatorUserInitiateWriteVoucherListing(worksheet2, col, row, p_hashMap);
            	}
            }
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue())
            {
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_VOUCHERSEGMENT_LIST);
            	if (list != null && list.size() > 0) {
            	row++;
            	col = 0;
            	row = this.BatchOperatorUserInitiateWriteVoucherSegmentListing(worksheet2, col, row, p_hashMap);
            	}
            }
            col = 0;
            row = 0;
            p_hashMap.put(PretupsI.BATCH_OPT_USR_CATEGORY_NAME, p_categoryVO.getCategoryName());
            this.BatchOperatorUserInitiateWriteInDataSheet(worksheet1, col, row, p_hashMap, p_categoryVO);

            // worksheet2.setProtected(true);

            workbook.write();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeExcel", " Exception e: " + e.getMessage());
        } finally {
            try {
                if (workbook != null)
                    workbook.close();
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            worksheet1 = null;
            worksheet2 = null;
            workbook = null;
            if (_log.isDebugEnabled())
                _log.debug("writeExcel", " Exiting");
        }
    }
    
    private int BatchOperatorUserInitiateWriteUserPrefix(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException{
        if (_log.isDebugEnabled())
            _log.debug("writeUserPrifix", " p_hashMap size=" + p_hashMap.size() + ", p_locale: " + p_locale + ", col=" + col + ", row=" + row);
        final String METHOD_NAME = "writeUserPrefix";
        try {
            String keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.prefixheading", null);

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.prefixheading.note", null);
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.prefixcode", null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.prefixname", null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USER_PREFIX_LIST);
            ListValueVO listValueVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserPrifix", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserPrifix", " Exception e: " + e.getMessage());
            throw e;
        }
    }
    
    private int BatchOperatorUserInitiateWriteStausType(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeStausType", " p_hashMap size=" + p_hashMap.size() + ", p_locale: " + p_locale + ", col=" + col + ", row=" + row);
        final String METHOD_NAME = "writeStausType";
        try {
            String keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.status", null);

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.status.note", null);
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.statustype", null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.statusname", null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_STATUS_LIST);
            ListValueVO listValueVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeStausType", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeStausType", " Exception e: " + e.getMessage());
            throw e;
        }
    }
    
    private int BatchOperatorUserInitiateWriteDivisionDepartmentListing(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeDivisionDepartmentListing", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        final String METHOD_NAME = "writeDivisionDepartmentListing";
        try {
            String keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.divdept", null);
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.divdept.note", null);
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.divisiontype", null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.divisionname", null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.departmenttype", null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.departmentname", null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_DIVDEPT_LIST);
            BatchOPTUserVO batchOPTUserVO = null;
            ArrayList divList = new ArrayList();
            if (list != null && list.size() > 0) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    batchOPTUserVO = (BatchOPTUserVO) list.get(i);
                    if (batchOPTUserVO.getDivdeptID().equals(batchOPTUserVO.getParentID())) {
                        divList.add(batchOPTUserVO);
                    }
                }
            }

            row++;
            col = 0;
            BatchOPTUserVO batchOPTUserVO1 = null;
            if (divList != null && divList.size() > 0) {
                for (int k = 0, l = divList.size(); k < l; k++) {
                    batchOPTUserVO = (BatchOPTUserVO) divList.get(k);
                    label = new Label(col, row, batchOPTUserVO.getDivdeptID());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, batchOPTUserVO.getDivdeptName());
                    worksheet2.addCell(label);
                    row++;
                    for (int i = 0, j = list.size(); i < j; i++) {
                        batchOPTUserVO1 = (BatchOPTUserVO) list.get(i);
                        if (batchOPTUserVO.getDivdeptID().equals(batchOPTUserVO1.getParentID()) && !batchOPTUserVO1.getDivdeptID().equals(batchOPTUserVO1.getParentID())) {
                            label = new Label(col + 2, row, batchOPTUserVO1.getDivdeptID());
                            worksheet2.addCell(label);
                            label = new Label(col + 3, row, batchOPTUserVO1.getDivdeptName());
                            worksheet2.addCell(label);
                            row++;
                        }
                    }
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeDivisionDepartmentListing", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeDivisionDepartmentListing", " Exception e: " + e.getMessage());
            throw e;
        }

    }
    
    private int BatchOperatorUserInitiateWriteGeographyListing(WritableSheet worksheet2, int col, int row, HashMap p_hashMap,CategoryVO categoryVO) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeGeographyListing", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        final String METHOD_NAME = "writeGeographyListing";
        try {
        	String keyName=null;
        	if((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(categoryVO.getCategoryCode()))||(TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(categoryVO.getCategoryCode())))
        	{
        		keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.networklist", null);
        	}
        	else
        	{
        		keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.network", null);
        	}	
        	Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            if((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(categoryVO.getCategoryCode()))||(TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(categoryVO.getCategoryCode())))
        	{
            	keyName = RestAPIStringParser.getMessage(p_locale,"user.initiatebatchoperatoruser.mastersheet.networklist.note", null);
        	}
            else
            {
            	keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.network.note", null);
            }
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            if((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(categoryVO.getCategoryCode()))||(TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(categoryVO.getCategoryCode())))
        	{
            	keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.networklist.networkcode", null);
        	}
            else
            {
            	keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.networkcode", null);
            }
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            if(!((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(categoryVO.getCategoryCode()))||(TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(categoryVO.getCategoryCode()))))
        	{
            	keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.networkname", null);
            	label = new Label(col + 1, row, keyName, times16format);
                worksheet2.addCell(label);
        	}
            
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_GEOGRAPHY_LIST);
            if (list != null && list.size() > 0) {
                UserGeographiesVO userGeographiesVO = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    userGeographiesVO = (UserGeographiesVO) list.get(i);
                    label = new Label(col, row, userGeographiesVO.getGraphDomainCode());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, userGeographiesVO.getGraphDomainName());
                    worksheet2.addCell(label);
                    row++;
                    col = 0;
                }
            }

            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGeographyListing", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGeographyListing", " Exception e: " + e.getMessage());
            throw e;
        }

    }
    
    private int BatchOperatorUserInitiateWriteRolesList(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeRolesList", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        final String METHOD_NAME = "writeRolesList";
        try {
            String keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assignroles", null);
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assignroles.note", null);
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            // Logic for generating headings
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.rolesgroupname", null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assignrolestype", null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assignrolesname", null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            HashMap hm = (HashMap) p_hashMap.get(PretupsI.BATCH_OPT_USR_ASSIGN_ROLES);
            UserRolesVO userRolesVO = null;

            if (hm != null && hm.size() > 0) {
                String key = null;
                Set set = hm.keySet();
                Iterator itr = set.iterator();
                ArrayList rolesList = null;
                while (itr.hasNext()) {
                    key = (String) itr.next();
                    col = 0;
                    label = new Label(col, row, key + ":");
                    worksheet2.addCell(label);
                    row++;
                    rolesList = (ArrayList) hm.get(key);
                    Iterator rolesListItr = rolesList.iterator();
                    while (rolesListItr.hasNext()) {
                        userRolesVO = (UserRolesVO) rolesListItr.next();
                        if (userRolesVO.getGroupRole().equalsIgnoreCase("Y")) {
                            row--;
                            break;
                        }
                        col = 0;
                        label = new Label(col + 1, row, userRolesVO.getRoleCode());
                        worksheet2.addCell(label);
                        label = new Label(col + 2, row, userRolesVO.getRoleName());
                        worksheet2.addCell(label);
                        row++;
                    }
                    if (userRolesVO.getGroupRole().equalsIgnoreCase("Y")) {
                        label = new Label(0, row, "");
                        worksheet2.addCell(label);
                    }
                    if (_log.isDebugEnabled())
                        _log.debug("writeRolesList", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale);
                }
            }

            row++;
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assigngrouproles", null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assigngrouproles.note", null);
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            // Logic for generating headings
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.rolesgroupname", null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assigngrouproletype", null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.assigngrouprolename", null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            if (hm != null && hm.size() > 0) {
                String key = null;
                Set set = hm.keySet();
                Iterator itr = set.iterator();
                ArrayList rolesList = null;
                while (itr.hasNext()) {
                    key = (String) itr.next();
                    col = 0;
                    label = new Label(col, row, key + ":");
                    worksheet2.addCell(label);
                    row++;
                    rolesList = (ArrayList) hm.get(key);
                    Iterator rolesListItr = rolesList.iterator();
                    while (rolesListItr.hasNext()) {
                        userRolesVO = (UserRolesVO) rolesListItr.next();
                        if (userRolesVO.getGroupRole().equalsIgnoreCase("N")) {
                            row--;
                            break;
                        }
                        col = 0;
                        label = new Label(col + 1, row, userRolesVO.getRoleCode());
                        worksheet2.addCell(label);
                        label = new Label(col + 2, row, userRolesVO.getRoleName());
                        worksheet2.addCell(label);
                        row++;
                    }
                    if (userRolesVO.getGroupRole().equalsIgnoreCase("N")) {
                        label = new Label(0, row, "");
                        worksheet2.addCell(label);
                    }
                    if (_log.isDebugEnabled())
                        _log.debug("writeRolesList", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale);
                }
            }

            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeRolesList", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeRolesList", " Exception e: " + e.getMessage());
            throw e;
        }
    }
    
    private int BatchOperatorUserInitiateWriteDomainListing(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeDomainListing", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        final String METHOD_NAME = "writeDomainListing";
        try {
            String keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.domain", null);
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.domain.note", null);
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            // Logic for generating headings
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_DOMAIN_LIST);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.domaincode", null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.domainname", null);
            label = new Label(col + 1, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            if (list != null && list.size() > 0) {
                DomainVO domainVO = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    domainVO = (DomainVO) list.get(i);
                    label = new Label(col, row, domainVO.getDomainCodeforDomain());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, domainVO.getDomainName());
                    worksheet2.addCell(label);
                    row++;
                    col = 0;
                }
            }

            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeDomainListing", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeDomainListing", " Exception e: " + e.getMessage());
            throw e;
        }

    }
    
    private int BatchOperatorUserInitiateWriteProductListing(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeProductListing", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        final String METHOD_NAME = "writeProductListing";
        try {
            String keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.product", null);
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.product.note", null);
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.productcode", null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.productname", null);
            label = new Label(col + 1, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            // Logic for generating headings
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_PRODUCT_LIST);

            ListValueVO listValueVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }

            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeProductListing", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeProductListing", " Exception e: " + e.getMessage());
            throw e;
        }

    }
    
    private int BatchOperatorUserInitiateWriteVoucherListing(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeVoucherListing", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        final String METHOD_NAME = "writeVoucherListing";
        try {
        	String keyName=null;
        	
			keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.voucher", null);
        	
        	Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
           
			keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.voucher.note", null);
            
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchertype", null);
            
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            
            	keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchername", null);
            	label = new Label(col + 1, row, keyName, times16format);
                worksheet2.addCell(label);
        	
            
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_VOUCHERTYPE_LIST);
            if (list != null && list.size() > 0) {
                ListValueVO listValueVO = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                    col = 0;
                }
            }

            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeVoucherListing", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeVoucherListing", " Exception e: " + e.getMessage());
            throw e;
        }

    }
    
    private int BatchOperatorUserInitiateWriteVoucherSegmentListing(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
    	final String methodName = "writeVoucherSegmentListing";
    	if (_log.isDebugEnabled())
            _log.debug(methodName, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        try {
        	String keyName=null;
        	
			keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.voucher.segment", null);
        	
        	Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
			keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.voucher.note", null);
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchersegment", null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
        	keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchersegment.name", null);
        	label = new Label(col + 1, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_VOUCHERSEGMENT_LIST);
            if (list != null && list.size() > 0) {
                ListValueVO listValueVO = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                    col = 0;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw e;
        }

    }
    
    private void BatchOperatorUserInitiateWriteInDataSheet(WritableSheet worksheet1, int col, int row, HashMap p_hashMap, CategoryVO p_categoryVO) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeInDataSheet", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row + " =p_categoryVO=" + p_categoryVO);
        final String METHOD_NAME = "writeInDataSheet";
        try {
			String arr[] = {(String) p_hashMap.get(PretupsI.BATCH_OPT_USR_CATEGORY_NAME)};
            String keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.template.heading", arr);
            String comment = null;
            Label label = new Label(col, row, keyName, times12format);
            worksheet1.mergeCells(col, row, col + 10, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.header.downloadedby", null);
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_OPT_USR_CREATED_BY));
            worksheet1.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.header.categoryname", null);
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_OPT_USR_CATEGORY_NAME));
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.strar", null);
            label = new Label(++col, row, keyName);
            worksheet1.mergeCells(col, row, col + 5, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.usernameprefix", null);
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.usernameprefix.comment", null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Modified by deepika aggarwal
            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED))).booleanValue()) {
                keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.username", null);
                label = new Label(col++, row, keyName, times16format);
                // add comment
                comment = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.username.comment", null);
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet1.addCell(label);
            } else {
                keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.firstname", null);
                label = new Label(col++, row, keyName, times16format);
                // add comment
                comment = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.firstname.comment", null);
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet1.addCell(label);

                keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.lastname", null);
                label = new Label(col++, row, keyName, times16format);
                // add comment
                comment = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.lastname.comment", null);
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet1.addCell(label);
            }

            // end Modified by deepika aggarwal

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.loginid", null);
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.loginid.comment", null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.password", null);
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.password.comment", null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.mobilenumber", null);
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.mobilenumber.comment", null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.subscribercode", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.status", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.division", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.department", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            if(TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(p_categoryVO.getCategoryCode()) || TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(p_categoryVO.getCategoryCode()))
            {
            	keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.networkcode", null);
            	label = new Label(col++, row, keyName, times16format);
            	// add comment
                comment = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.networkcode.comment", null);
            }
            else
            {
            	keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.geographicaldomain", null);
            	label = new Label(col++, row, keyName, times16format);
            	// add comment
                comment = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.geographicaldomain.comment", null);
            }
            
            
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            if (PretupsI.YES.equalsIgnoreCase(p_categoryVO.getProductTypeAllowed())) {
                keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.products", null);
                label = new Label(col++, row, keyName, times16format);
                // add comment
                comment = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.products.comment", null);
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet1.addCell(label);
            }

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.roletype", null);
            ;
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.roletype.comment", null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.roles", null);
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.roles.comment", null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Added for Authentication Type
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTH_TYPE_REQ))).booleanValue()) {
                keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.authtype.allowed", null);
                label = new Label(col++, row, keyName, times16format);
                // add comment
                comment = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.authtype.allowed.comment", null);
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet1.addCell(label);
            }

            if (p_categoryVO.getDomainAllowed().equalsIgnoreCase(PretupsI.YES) && PretupsI.DOMAINS_ASSIGNED.equals(p_categoryVO.getFixedDomains())) {
                keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.domain", null);
                label = new Label(col++, row, keyName, times16format);
                // add comment
                comment = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.domain.comment", null);
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet1.addCell(label);
            }
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.designation", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.externalcode", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.contactnumber", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.ssn", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.address1", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.address2", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.city", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.state", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.country", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.xlsfile.details.email", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
            {
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchertype", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            }
            
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue())
            {
            keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchersegment", null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            }

            // /setting for the vertical freeze panes
            SheetSettings sheetSetting = new SheetSettings();
            sheetSetting = worksheet1.getSettings();
            sheetSetting.setVerticalFreeze(row + 1);

            // /setting for the horizontal freeze panes
            SheetSettings sheetSetting1 = new SheetSettings();
            sheetSetting1 = worksheet1.getSettings();
            sheetSetting1.setHorizontalFreeze(6);

        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheet", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheet", " Exception e: " + e.getMessage());
            throw e;
        }
    }

}
