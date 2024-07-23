package com.btsl.pretups.xl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.btsl.util.MessageResources;
//import org.apache.struts.util.PropertyMessageResources;

import com.btsl.util.BTSLUtil;
import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainTypeVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelFileConstants;
import com.web.pretups.channel.user.web.BatchUserUpdateAction;

import jxl.write.Label;

public class BatchUserCreationExcelRWPOI {
	private static Log _log = LogFactory.getLog(BatchUserCreationExcelRWPOI.class.getName());
	private final int COLUMN_MARGE = 10;
	private MessageResources p_messages = null;
	private Locale p_locale = null;
	
    

    protected static void setCellComment(Cell cell, String message) {
    	Drawing drawing = cell.getSheet().createDrawingPatriarch();
        CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();
        ClientAnchor anchor = factory.createClientAnchor();
        anchor.setCol1(cell.getColumnIndex());
        anchor.setCol2(cell.getColumnIndex() + 5);
        anchor.setRow1(cell.getRowIndex());
        anchor.setRow2(cell.getRowIndex() + 5);
        anchor.setDx1(100);
        anchor.setDx2(100);
        anchor.setDy1(100);
        anchor.setDy2(100);
        Comment comment = drawing.createCellComment(anchor);
        RichTextString str = factory.createRichTextString(message);
        comment.setString(str);
        comment.setAuthor("pretups");
        comment.setRow(cell.getRowIndex());
        comment.setColumn(cell.getColumnIndex());
        cell.setCellComment(comment);
    }
    
    //
    /**
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeUserPrefix(SXSSFSheet  worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeUserPrifix", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeUserPrefix";
          Row rowdata = null;
          Cell cell = null;
		  String keyName = null;
          try {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.prefixheading");
        	else
        	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.prefixheading",null);
            rowdata = worksheet2.createRow(row);
            //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			worksheet2.addMergedRegion(new CellRangeAddress(row,  row, col,(col+2)));
            row++;
            col = 0;
            rowdata = worksheet2.createRow(row);
			if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.prefixheading.note");
			else
            keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.prefixheading.note",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellValue(keyName);
			worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (COLUMN_MARGE)));
            row++;
            col = 0;
            
            rowdata = worksheet2.createRow(row);
			if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.prefixcode");
			else
            keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.prefixcode",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			col++;
			if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.prefixname");
			else
            keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.prefixname",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_USER_PREFIX_LIST);
            ListValueVO listValueVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellValue(listValueVO.getValue());
        			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col+1));
        			cell.setCellValue(listValueVO.getLabel());
                    row++;
                    rowdata = worksheet2.createRow(row);
                }
            }
            return row;
        }  catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserPrifix", " Exception e: " + e.getMessage());
        }
        return row;
    }
    private int writeGeographyListingForUpdate(SXSSFSheet  worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeGeographyListingForUpdate", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeGeographyListingForUpdate";
        Row rowdata = null;
        Cell cell = null;
        String keyName=null;
        try {
        	
        	if(!BTSLUtil.isNullObject(p_messages))
                keyName =  p_messages.getMessage(p_locale, "batchusercreation.mastersheet.avaliablegeographieslist");
            	else
            	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.avaliablegeographieslist",null);
        	
             
            rowdata = worksheet2.createRow(row);
            //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+2)));
            row++;
            col = 0;
            if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.avaliablegeographieslist.note");
            else
            	keyName = RestAPIStringParser.getMessage(p_locale,"batchusercreation.mastersheet.avaliablegeographieslist.note",null);
            rowdata = worksheet2.createRow(row);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
           	cell.setCellValue(keyName);
           	worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (COLUMN_MARGE)));
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;
            // Logic for generating headings
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GEOGRAPHY_LIST);
            UserGeographiesVO userGeographiesVO = null;
            ArrayList geoDomainTypeList = new ArrayList();

            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    userGeographiesVO = (UserGeographiesVO) list.get(i);
                    if (!geoDomainTypeList.contains(userGeographiesVO.getGraphDomainType())) {
                        geoDomainTypeList.add(userGeographiesVO.getGraphDomainType());
                    }
                }
            }
            // Generate Headings from the ArrayList
            String endTagCode =null;
            String endTagName = null;
            if(!BTSLUtil.isNullObject(p_messages)) {
            	endTagCode = p_messages.getMessage(p_locale, "code");
            	endTagName = p_messages.getMessage(p_locale, "name");
            }else {
            	endTagCode =PretupsRestUtil.getMessageString("code");
            	endTagName = PretupsRestUtil.getMessageString("name");
            }

            String geoType = null;
            for (int i = 0, j = geoDomainTypeList.size(); i < j; i++) {
            	col = 0;
                geoType = ((String) geoDomainTypeList.get(i)).trim();
                if(!BTSLUtil.isNullObject(p_messages)) {
                keyName = p_messages.getMessage(p_locale, geoType);
                }else {
                	keyName =  PretupsRestUtil.getMessageString(geoType);	
                }
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellStyle(cellStyle);
                cell.setCellValue(keyName + " " + endTagCode + "(" + geoType + ")");
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellStyle(cellStyle);
                cell.setCellValue(keyName + " " + endTagName);
                col++;
            }
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;
            int nameOccurance = 0;
            int oldseqNo = 0;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {

                    userGeographiesVO = (UserGeographiesVO) list.get(i);
                    if (oldseqNo > userGeographiesVO.getGraphDomainSequenceNumber()) {
                        nameOccurance--;
                    } else if (oldseqNo < userGeographiesVO.getGraphDomainSequenceNumber()) {
                        nameOccurance++;
                    }
                    col = 0;
                    if (userGeographiesVO.getGraphDomainSequenceNumber() == 2) {
                        col = 0;
                        nameOccurance = 0;
                    }
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                   	cell.setCellValue(userGeographiesVO.getGraphDomainCode());
                   	cell = rowdata.createCell(BTSLUtil.parseIntToShort(col+1));
                   	cell.setCellValue(userGeographiesVO.getGraphDomainName());
                    oldseqNo = userGeographiesVO.getGraphDomainSequenceNumber();
                    row++;
                    rowdata = worksheet2.createRow(row);
                }
            }

            return row;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGeographyListingForUpdate", " Exception e: " + e.getMessage());
        }
		return row;
    }
    private int writeLanguageList(SXSSFSheet  worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeLanguageList", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeLanguageList";
        Row rowdata = null;
        Cell cell = null;
		String keyName = null;
        try {
            if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.Languageheading");
        	else
        	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.Languageheading",null);
            rowdata = worksheet2.createRow(row);
            //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+2)));
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;
			if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.languageheading.note");
			else
            keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.languageheading.note",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (COLUMN_MARGE)));
			row++;
			col=0;
            rowdata = worksheet2.createRow(row);
			if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.language.code");
			else
            keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.language.code",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			
			col++;
			if(!BTSLUtil.isNullObject(p_messages))
			keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.language.name");
			else
			keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.language.name",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;

            HashMap map = (HashMap) p_hashMap.get(PretupsI.BATCH_USR_LANGUAGE_LIST);

            if (map.size() > 0) {
                Set set = map.entrySet();
                Iterator i = set.iterator();
                while (i.hasNext()) {
                    Map.Entry me = (Map.Entry) i.next();
                    String key = (String) me.getKey();
                    col = 0;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellValue(key);
        			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col+1));
        			cell.setCellValue(String.valueOf(me.getValue()));
                    row++;
                    rowdata = worksheet2.createRow(row);
                }
            }
            return row;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeLanguageList", " Exception e: " + e.getMessage());
        }
		return row;
    }


    /**
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeUserOutletSubOutlet(SXSSFSheet  worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeUserOutletSubOutlet", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeUserOutletSubOutlet";
        Row rowdata = null;
        Cell cell = null;
		String keyName = null;
        try {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotletheading");      
        	else
        	keyName = RestAPIStringParser.getMessage( p_locale,"batchusercreation.mastersheet.outletsubotletheading",null);
            rowdata = worksheet2.createRow(row);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+2)));
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;
			if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotletheading.note");
            else
            keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotletheading.note",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellValue(keyName);
			worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (COLUMN_MARGE)));
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;
			if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotlet.outletcode");
			else
            keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotlet.outletcode",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotlet.outletname");
			else
			keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotlet.outletname",null);
            col++;
			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotlet.suboutletcode");
			else
			keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotlet.suboutletcode",null);
            col++;
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotlet.suboutletname");
			else
			keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotlet.suboutletname",null);
            col++;
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;

            ArrayList outletList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_OUTLET_LIST);
            ArrayList suboutletList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_SUBOUTLET_LIST);
            ListValueVO listValueVO = null;
            ListValueVO listValueVOSub = null;
            if (outletList != null) {
                for (int i = 0, j = outletList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) outletList.get(i);
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellValue(listValueVO.getValue());
        			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col+1));
        			cell.setCellValue(listValueVO.getLabel());
                    if (suboutletList != null) {
                        for (int k = 0, l = suboutletList.size(); k < l; k++) {
                            listValueVOSub = (ListValueVO) suboutletList.get(k);
                            String sub[] = listValueVOSub.getValue().split(":");
                            if (listValueVO.getValue().equals(sub[1])) {
                            	cell = rowdata.createCell(BTSLUtil.parseIntToShort(col+2));
                    			cell.setCellValue(sub[0]);
                    			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col+3));
                    			cell.setCellValue(listValueVOSub.getLabel());
                                row++;
                                rowdata = worksheet2.createRow(row);
                            }
                        }
                    }
                    row++;
                    rowdata = worksheet2.createRow(row);
                }
            }
            return row;
        }  catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserOutletSubOutlet", " Exception e: " + e.getMessage());
        }
        return row;
    }

    // Added by Shashank Gaur for Trf rule Authentication
    /**
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeTrfRuleType(SXSSFSheet  worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellstyle) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeTrfRuleType", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeTrfRuleType";
        Row rowdata = null;
        Cell cell = null;
        String keyName = null;
        try {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.trfruletypeheading");
        	else
       		keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.trfruletypeheading",null);
            rowdata = worksheet2.createRow(row);
            //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellstyle);
			cell.setCellValue(keyName);
			worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+2)));
            row++;
            col = 0;
			if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.trfruletypeheading.note");
            else
            keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.trfruletypeheading.note",null);
            rowdata = worksheet2.createRow(row);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellValue(keyName);
			worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (COLUMN_MARGE)));
            row+=2;
            col = 0;
            if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.trfruletype.trfruletypecode");
            else
            	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.trfruletype.trfruletypecode",null);
            rowdata = worksheet2.createRow(row);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellstyle);
			cell.setCellValue(keyName);
			col++;
			if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.trfruletype.trfruletypename");
			else
			keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.trfruletype.trfruletypename",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellstyle);
			cell.setCellValue(keyName);
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;

            ArrayList outletList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_TRF_RULE_LIST);
            ListValueVO listValueVO = null;
            if (outletList != null) {
                for (int i = 0, j = outletList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) outletList.get(i);
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellValue(listValueVO.getValue());
        			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col+1));
        			cell.setCellValue(listValueVO.getLabel());
                    row++;
                    rowdata = worksheet2.createRow(row);
                }
            }
            return row;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeTrfRuleType", " Exception e: " + e.getMessage());
        }
        return row;
    }
    private int writeLmsProfile(SXSSFSheet  worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeLmsProfile", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeLmsProfile";
        Row rowdata = null;
        Cell cell = null;
        String keyName =null;
        try {
        	if(!BTSLUtil.isNullObject(p_messages))
        		keyName=  p_messages.getMessage(p_locale, "batchusercreation.mastersheet.lmsprofile");
        	else 
        		keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.lmsprofile",null);
        	
            rowdata = worksheet2.createRow(row);
            //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;
            if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.lmsprofile.note");
            else
            keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.lmsprofile.note",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellValue(keyName);
			row++; 
			col = 0;
			rowdata = worksheet2.createRow(row);
			if(!BTSLUtil.isNullObject(p_messages))
				keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.profileid");
			else
			 keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.profileid",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			col++;
			if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.profilename");
			else
			keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.profilename",null);
			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;

            ArrayList lmsProfileList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_LMS_PROFILE);
            ListValueVO listValueVO = null;
            if (lmsProfileList != null) {
                for (int i = 0, j = lmsProfileList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) lmsProfileList.get(i);
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellValue(listValueVO.getValue());
        			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col+1));
        			cell.setCellValue(listValueVO.getLabel());
                    row++;
                    rowdata = worksheet2.createRow(row);
                }
            }
            return row;
        }catch (Exception exception) {
            _log.errorTrace(METHOD_NAME, exception);
            _log.error("writeLmsProfile", " Exception exception: " + exception.getMessage());
        }
		return row;
    }

    /**
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeServiceType(SXSSFSheet  worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle){
        if (_log.isDebugEnabled()) {
            _log.debug("writeServiceType", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeServiceType";
        Row rowdata = null;
        Cell cell = null;
        String keyName = null;
        try {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.service");
        	else
        	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.service",null);
            rowdata = worksheet2.createRow(row);
            //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+2)));
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;
            if(!BTSLUtil.isNullObject(p_messages))            
			keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.service.note");
            else
            keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.service.note",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellValue(keyName);
			worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (COLUMN_MARGE)));
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;
            if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.servicetype");
            else
            keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.servicetype",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			col++;
			if(!BTSLUtil.isNullObject(p_messages))            
			keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.servicename");
			else
			keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.servicename",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_SERVICE_LIST);
            ListValueVO listValueVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellValue(listValueVO.getValue());
        			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col+1));
        			cell.setCellValue(listValueVO.getLabel());
                    row++;
                    rowdata = worksheet2.createRow(row);
                }
            }
            return row;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeServiceType", " Exception e: " + e.getMessage());
        }
		return row;
    }
    private int writeRoleCode(SXSSFSheet  worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeRoleCode", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeRoleCode";
        Row rowdata = null;
        Cell cell = null;
        String keyName = null;
        try {
        	
        	  if(!BTSLUtil.isNullObject(p_messages))
                keyName =p_messages.getMessage(p_locale, "bulkuser.modify.mastersheet.systemrolecodedetails");
            	else
            	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.modify.mastersheet.systemrolecodedetails",null);
        	
             
            rowdata = worksheet2.createRow(row);
            //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+2)));
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;

            if(!BTSLUtil.isNullObject(p_messages))
            	keyName = p_messages.getMessage(p_locale, "bulkuser.modify.mastersheet.rolecode");
            	else
            	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.modify.mastersheet.rolecode",null);
            
            
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			
			if(!BTSLUtil.isNullObject(p_messages))
				keyName = p_messages.getMessage(p_locale, "bulkuser.modify.mastersheet.rolename");
            	else
            	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.modify.mastersheet.rolename",null);
			
            
            col++;
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_ROLE_CODE_LIST);
            UserRolesVO rolesVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    rolesVO = (UserRolesVO) list.get(i);
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellValue(rolesVO.getRoleCode());
        			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col+1));
        			cell.setCellValue(rolesVO.getRoleName());
                    row++;
                    rowdata = worksheet2.createRow(row);
                }
            }
            return row;
        }catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeRoleCode", " Exception e: " + e.getMessage());
        }
		return row;

    }
    
    private int writeGroupRoleCode(SXSSFSheet  worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeGroupRoleCode", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeGroupRoleCode";
        Row rowdata = null;
        Cell cell = null;
        String keyName = null;
        try {
        	
        	
        	if(!BTSLUtil.isNullObject(p_messages))
        		p_messages.getMessage(p_locale, "bulkuser.modify.mastersheet.grouprolecodedetails");
            	else
            	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.modify.mastersheet.grouprolecodedetails",null);
            
            rowdata = worksheet2.createRow(row);
            //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+2)));
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;
            if(!BTSLUtil.isNullObject(p_messages))
            	keyName = p_messages.getMessage(p_locale, "bulkuser.modify.mastersheet.rolecode");
            	else
            	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.modify.mastersheet.rolecode",null);

            
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
            keyName = p_messages.getMessage(p_locale, "bulkuser.modify.mastersheet.rolename");
            col++;
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GROUP_ROLE_LIST);
            UserRolesVO rolesVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    rolesVO = (UserRolesVO) list.get(i);
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellValue(rolesVO.getRoleCode());
        			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col+1));
        			cell.setCellValue(rolesVO.getRoleName());
                    row++;
                    rowdata = worksheet2.createRow(row);
                }
            }
            return row;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGroupRoleCode", " Exception e: " + e.getMessage());
        }
		return row;

    }

    private int writeRsaAssociations(SXSSFSheet  worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeRSAAllowed", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeRsaAssociations";
        Row rowdata = null;
        Cell cell = null;
        String keyName = null;
        try {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.rsaheading");
        	else
        		keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.rsaheading",null);
            rowdata = worksheet2.createRow(row);
            //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+2)));
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;
            if(!BTSLUtil.isNullObject(p_messages))            
			keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.rsaheading.note");
            else
            keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.rsaheading.note",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellValue(keyName);
            worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (COLUMN_MARGE)));
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;
            if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categorycode");
            else
            keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.categorycode",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            col++;
            if(!BTSLUtil.isNullObject(p_messages))            
			keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.isrsaallowed");
			else
            keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.isrsaallowed",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            row++;
            rowdata = worksheet2.createRow(row);
            col = 0;

            ArrayList categoryList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_LIST);
            CategoryVO categoryVO;
            int catSize = 0;
            if (categoryList != null) {
                catSize = categoryList.size();
            }

            if (categoryList != null && (catSize = categoryList.size()) > 0) {
                for (int i = 0; i < catSize; i++) // Prints One Row Of Category
                {
                    col = 0;
                    categoryVO = (CategoryVO) categoryList.get(i);
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                    cell.setCellValue(categoryVO.getCategoryName());
                    Boolean rsarequired = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.RSA_AUTHENTICATION_REQUIRED, "TG", categoryVO.getCategoryCode())).booleanValue();
                    if (rsarequired == true) {
                    	col++;
                    	cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                        cell.setCellValue(PretupsI.YES);
                    } else {;
                    	col++;
                    	cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                        cell.setCellValue(PretupsI.NO);
                    }
                    row++;
                    rowdata = worksheet2.createRow(row);
                    col = 0;
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeRsaAssociations", " Exception e: " + e.getMessage());
        }
        return row;
    }
    
    //Reading
    /**
     * @param p_excelID
     * @param p_fileName
     * @param p_readLastSheet
     * @param p_leftHeaderLinesForEachSheet
     * @param map
     * @return
     * @throws Exception
     */
    public String[][] readMultipleExcelSheet(String p_excelID, String p_fileName, boolean p_readLastSheet, int p_leftHeaderLinesForEachSheet, HashMap<String, String> map) {
        if (_log.isDebugEnabled()) {
            _log.debug("readMultipleExcelSheet", " p_excelID: " + p_excelID + " p_fileName: " + p_fileName + " p_readLastSheet=" + p_readLastSheet, " p_leftHeaderLinesForEachSheet=" + p_leftHeaderLinesForEachSheet);
        }
        final String METHOD_NAME = "readMultipleExcelSheet";
        String strArr[][] = null;
        int arrRow = p_leftHeaderLinesForEachSheet;
        XSSFWorkbook  workbook = null;
        XSSFSheet  excelsheet = null;
        int noOfSheet = 0;
        int noOfRows = 0;
        int noOfcols = 0;
        InputStream fileInStream = null;
        try {
        	fileInStream = new FileInputStream(p_fileName);
            workbook = new XSSFWorkbook (fileInStream);
            noOfSheet = workbook.getNumberOfSheets();
            if (!p_readLastSheet) {
                noOfSheet = noOfSheet - 1;
            }
            // Total number of rows in the excel sheet
            for (int i = 0; i < noOfSheet; i++) {
                excelsheet = workbook.getSheetAt(i);
                noOfRows = noOfRows + (excelsheet.getLastRowNum()+1- p_leftHeaderLinesForEachSheet);
                noOfcols = excelsheet.getRow(p_leftHeaderLinesForEachSheet-1).getLastCellNum();
                XSSFRow xrow = excelsheet.getRow(p_leftHeaderLinesForEachSheet - 1);
                int countOfCols = xrow.getLastCellNum();
            }
            // Initialization of string array
            strArr = new String[noOfRows + p_leftHeaderLinesForEachSheet][noOfcols];
            for (int i = 0; i < noOfSheet; i++) {            	
                excelsheet = workbook.getSheetAt(i);
                noOfRows = excelsheet.getLastRowNum()+1;
                noOfcols = excelsheet.getRow(p_leftHeaderLinesForEachSheet-1).getLastCellNum();
               // Cell cell = null;
                String content = null;
                String key = null;
                int[] indexMapArray = new int[noOfcols];
                String indexStr = null;
                for (int k = 0; k < p_leftHeaderLinesForEachSheet; k++) {
                    for (int col = 0; col < noOfcols; col++)
                    {
                        indexStr = null;
                        key = ExcelFileConstants.getReadProperty(p_excelID, String.valueOf(col));
                        if (key == null) {
                            key = String.valueOf(col);
                        }
                        indexStr = ExcelFileConstants.getReadProperty(p_excelID, String.valueOf(col));
                        if (indexStr == null) {
                            indexStr = String.valueOf(col);
                        }
                        indexMapArray[col] = Integer.parseInt(indexStr);
                        strArr[k][indexMapArray[col]] = key;
                    }
                }
                XSSFRow row; 
        		XSSFCell cell;
        		Iterator rows = excelsheet.rowIterator();
        		int rowCount = 0;
        		
        		while (rows.hasNext())
        		{   
        			if(rowCount < p_leftHeaderLinesForEachSheet) {
        			rowCount++;
        			row=(XSSFRow) rows.next();
        			continue;
        		    }
        			map.put(Integer.toString(arrRow + 1), workbook.getSheetName(i) + PretupsI.ERROR_LINE + (rowCount + 1));
        			row=(XSSFRow) rows.next();
        			Iterator cells = row.cellIterator();
        			int colCount = 0;
        			for(colCount=0; colCount < noOfcols; colCount++) {
        				
        				 if( row.getCell(colCount) == null ){
                             cell = row.createCell(colCount);
                         } else {
                             cell = row.getCell(colCount);
                         }
        				 content = cell.getStringCellValue();
                         content = content.replaceAll("\n", " ");
                         content = content.replaceAll("\r", " ");
                         strArr[arrRow][indexMapArray[colCount]] = content;
        			}
        			rowCount++;
        			arrRow++;
        		}
        		
            }
            return strArr;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("readMultipleExcelSheet", " Exception e: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    fileInStream.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            workbook = null;
            excelsheet = null;
            if (_log.isDebugEnabled()) {
                _log.debug("readMultipleExcelSheet", " Exiting strArr: " + strArr);
            }
        }
        return strArr;
    }

    
    
/**
 * @param p_excelID
 * @param p_hashMap
 * @param messages
 * @param locale
 * @param p_fileName
 * @throws Exception
 */
public void writeUserCreateExcel (String p_excelID, HashMap p_hashMap, MessageResources messages, Locale locale, String p_fileName) {
	final String METHOD_NAME = "writeUserCreateExcel";
    if (_log.isDebugEnabled()) {
        _log.debug(METHOD_NAME, " p_excelID: " + p_excelID + " p_hashMap:" + p_hashMap + " p_locale: " + locale + " p_fileName: " + p_fileName);
    }
    boolean batchUserProfileAssign = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN);
    boolean rsaAuthenticationRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RSA_AUTHENTICATION_REQUIRED);
    boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
    boolean userVoucherTypeAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
    boolean ptupsMobqutyMergd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD);
    SXSSFSheet  worksheet1 = null, worksheet2 = null;
    int sheetCount = 0;
    int col = 0;
    int row = 0;
    int stepSize = 0;
    Row rowdata = null;
	Cell cell = null;
    String noOfRowsInOneTemplate = null; // No. of users data in one sheet
    FileOutputStream outputStream = null;
    SXSSFWorkbook  workbook = null;
    CellStyle style = null;
    CreationHelper factory = null;
    int masterRow = 0; int masterCol = 0;
    String userType = "";
    double t_mem = 0;
    double f_mem = 0;

    try {
        t_mem = Runtime.getRuntime().totalMemory() / 1048576;
        f_mem = Runtime.getRuntime().freeMemory() / 1048576;
        _log.info(METHOD_NAME, "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));

        workbook = new SXSSFWorkbook();  
    	workbook.setCompressTempFiles(true);
    	factory = workbook.getCreationHelper();
    	style = workbook.createCellStyle();
    	outputStream = new FileOutputStream(p_fileName);
    	Font times16font = workbook.createFont();
    	times16font.setFontName("Arial");
    	//times16font.setBoldweight((short) 8);
    	//times16font.setFontHeightInPoints((short) 12);
    	times16font.setFontHeightInPoints(BTSLUtil.parseIntToShort(12));
    	times16font.setItalic(true);
    	times16font.setBold(true);
    	style.setFont(times16font);
    	p_messages = messages;
    	p_locale = locale;
        String keyName = null;
        worksheet1 =  workbook.createSheet("Template Sheet");
        worksheet2 =  workbook.createSheet("Master Sheet");
        worksheet2.setRandomAccessWindowSize(1000);
        Label label = null;
        final String arr[] = { (String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME)};
        if(!BTSLUtil.isNullObject(p_messages))
	    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.heading", (String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME));
		else
        keyName = RestAPIStringParser.getMessage(p_locale,"batchusercreation.mastersheet.heading", arr);
        
        rowdata = worksheet2.createRow(masterRow);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(masterCol));
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
        worksheet2.addMergedRegion(new CellRangeAddress(masterRow, masterRow, masterCol, (masterCol + 1)));
        masterRow++;
        masterRow++;
        masterCol = 0;
        masterRow = this.writeUserPrefix(worksheet2, masterCol, masterRow, p_hashMap,style);
        
        masterRow++;
        masterCol = 0;
        masterRow = this.writeUserOutletSubOutlet(worksheet2, masterCol, masterRow, p_hashMap,style);
        masterRow++;
        masterCol = 0;
        masterRow = this.writeServiceType(worksheet2, masterCol, masterRow, p_hashMap,style);
        masterRow++;
        masterCol = 0;
        masterRow = this.writeGeographyListing(worksheet2, masterCol, masterRow, p_hashMap,style);
        masterRow++;
        masterCol = 0;
        masterRow = this.writeCategoryHierarchy(worksheet2, masterCol, masterRow, p_hashMap,style);
        masterRow++;
        masterCol = 0;
        masterRow = this.writeCategoryData(worksheet2, masterCol, masterRow, p_hashMap,style);
        masterRow++;
        masterCol = 0;
        masterRow = this.writeCommissionData(worksheet2, masterCol, masterRow, p_hashMap,style);

        masterRow++;
        masterCol = 0;
        masterRow = this.writeLanguageList(worksheet2, masterCol, masterRow, p_hashMap,style);

        masterRow++;
        masterCol = 0;
        masterRow = this.writeGradeData(worksheet2, masterCol, masterRow, p_hashMap,style);
        userType = (String) p_hashMap.get(PretupsI.USER_TYPE);
        if ((userType != null) && (userType.equals(PretupsI.OPERATOR_USER_TYPE) || (userType.equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign))) {
            if (rsaAuthenticationRequired) {
            	masterRow++;
                masterCol = 0;
                masterRow = this.writeRsaAssociations(worksheet2, masterCol, masterRow, p_hashMap,style);
                masterRow++;
            }
            if (ptupsMobqutyMergd) {
            	masterRow++;
                masterCol = 0;
                masterRow = this.writeMpayProfID(worksheet2, masterCol, masterRow, p_hashMap,style);
            }

            if (isTrfRuleUserLevelAllow) {
            	masterRow++;
                masterCol = 0;
                masterRow = this.writeTrfRuleType(worksheet2, masterCol, masterRow, p_hashMap,style);
            }
        }
        masterRow++;
        masterCol = 0;
        masterRow = this.writeUserDocumentTypeList(worksheet2, masterCol, masterRow, p_hashMap,style);
        masterRow++;
        masterCol = 0;
        masterRow = this.writeUserPaymentTypeList(worksheet2, masterCol, masterRow, p_hashMap,style);
        if (SystemPreferences.USERWISE_LOAN_ENABLE) {
        masterRow++;
        masterCol = 0;
        masterRow = this.writeUserLoanProfile(worksheet2, masterCol, masterRow, p_hashMap,style);
        }
        
        if(userVoucherTypeAllowed)
        {
        	ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_VOUCHERTYPE_LIST);
        	if (list != null && list.size() > 0) {
        		row++;
        		col = 0;
        		row = this.writeVoucherListing(worksheet2, masterCol, masterRow, p_hashMap, style);
        	} 
        }
        col = 0;
        row = 0;
        this.writeInDataSheet(worksheet1, col, row, p_hashMap,style);
        workbook.write(outputStream);
        outputStream.close();
        workbook.dispose();
    } catch (Exception e) {
        _log.errorTrace(METHOD_NAME, e);
        _log.error(METHOD_NAME, " Exception e: " + e.getMessage());
    } finally {
        try	 {
            if (workbook != null) {
                workbook.close();
            }
            if (outputStream != null) {
       		 outputStream.close();
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        worksheet1 = null;
        worksheet2 = null;
        workbook = null;
        t_mem = Runtime.getRuntime().totalMemory() / 1048576;
        f_mem = Runtime.getRuntime().freeMemory() / 1048576;
        _log.info(METHOD_NAME, "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Exiting");
        }
    }
}

	/**
     * Method writeCommissionData
     * This method writes the Commission details 
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeCommissionData(SXSSFSheet worksheet2, int col, int row, HashMap p_hashMap ,  CellStyle cellStyle) {
        row += 2; // to give some space before writing the next records
        boolean batchUserProfileAssign = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN);
        Row rowdata = null;
        Cell cell = null;
        if (_log.isDebugEnabled()) {
            _log.debug("writeCommissionData", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        String keyName= null;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.commissionheading");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.commissionheading",null);
        rowdata = worksheet2.createRow(row);
        //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+8)));
        row++;
        col = 0;
        if(!BTSLUtil.isNullObject(p_messages))
		keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.commissionheading.note");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.commissionheading.note",null);
        rowdata = worksheet2.createRow(row);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        row++;
        col = 0;
		if(!BTSLUtil.isNullObject(p_messages))
		keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categorycode");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.categorycode",null);
        rowdata = worksheet2.createRow(row);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryname");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.categoryname",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grphcode");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.grphcode",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grphname");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.grphname",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        row++;
        if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.gradecode");
        	else
            	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.grphcode",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            col++;
            if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.gradename");
            else
            	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.gradename",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            col++;
            if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.commisionprofilecode");
            else
            	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.commisionprofilecode",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            col++;
            if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.commisionprofilename");
            else
            	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.commisionprofilename",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            col++;
        }
        col = 0;
// Iteration Starts from Row number
        
        ArrayList commProfList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_COMM_LIST);
        int catSize = 0;
// Equate The Sizes of the list first so that it will not be checked in
        // the loop
        CommissionProfileSetVO CSVO = null;
        if (commProfList != null && (catSize = commProfList.size()) > 0) {
            for (int i = 0; i < catSize; i++) // Prints One Row Of Category
            {
                col = 0;
                rowdata = worksheet2.createRow(row);
                CSVO = (CommissionProfileSetVO) commProfList.get(i);
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(CSVO.getCategoryCode());
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(CSVO.getCategoryName());
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(CSVO.getGrphDomainCode());
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(CSVO.getGrphDomainName());
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(CSVO.getGradeCode());
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(CSVO.getGradeName());
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(CSVO.getCommProfileSetId());
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(CSVO.getCommProfileSetName());
                col++;
                row++;
            }
        }
        return row;
    }
	
	
	/**
     * Method writeGradeData
     * This method writes the Grade details 
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeGradeData(SXSSFSheet worksheet2, int col, int row, HashMap p_hashMap ,  CellStyle cellStyle) {
        row += 2; // to give some space before writing the next records
        Row rowdata = null;
        Cell cell = null;
        if (_log.isDebugEnabled()) {
            _log.debug("writeGradeData", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        String keyName = null;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.gradeheading");
        else
        	 keyName = RestAPIStringParser.getMessage(p_locale,"batchusercreation.mastersheet.gradeheading",null);
        rowdata = worksheet2.createRow(row);
        //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+5)));
        row++;
        col = 0;
        rowdata = worksheet2.createRow(row);
        if(!BTSLUtil.isNullObject(p_messages))        
		keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categorycode");
        else
           	 keyName = RestAPIStringParser.getMessage(p_locale,"batchusercreation.mastersheet.categorycode",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryname");
        else
       	 keyName = RestAPIStringParser.getMessage(p_locale,"batchusercreation.mastersheet.categoryname",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))        
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grade");
        else
       	 keyName = RestAPIStringParser.getMessage(p_locale,"batchusercreation.mastersheet.grade",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+1)));
        row++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.gradecode");
        else
       	 keyName = RestAPIStringParser.getMessage(p_locale,"batchusercreation.mastersheet.gradecode",null);
        rowdata = worksheet2.createRow(row);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.gradename");
        else
       	 keyName = RestAPIStringParser.getMessage(p_locale,"batchusercreation.mastersheet.gradename",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        row++;;
// Iteration Starts from Row number
        ArrayList gradeList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GRADE_LIST);
        int catSize = 0;
        GradeVO gradeVO = null;
        if (gradeList != null && (catSize = gradeList.size()) > 0) {
            for (int i = 0; i < catSize; i++) // Prints One Row Of Category
            {
                col = 0;
                rowdata = worksheet2.createRow(row);
                gradeVO = (GradeVO) gradeList.get(i);
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(gradeVO.getCategoryCode());        
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(gradeVO.getCategoryName());        
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(gradeVO.getGradeCode());        
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(gradeVO.getGradeName());        
                row++;
            }
        }
        return row;
    }
    /**
     * Method writeCategoryData
     * This method writes the category details including
     * geographies,grade,commision profile,transfer
     * profile & group information
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeCategoryData(SXSSFSheet worksheet2, int col, int row, HashMap p_hashMap,  CellStyle cellStyle) {
        int maxrow = row;
        Row rowdata = null;
        Cell cell = null;
		String keyName = null;
        if (_log.isDebugEnabled()) {
            _log.debug("writeCategoryData", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        boolean batchUserProfileAssign = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN);
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryheading");
        else
        keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.categoryheading",null);
        rowdata = worksheet2.createRow(row);
        //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+2)));
        row++;
        col = 0;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryheading.note");
        else
        keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.categoryheading.note",null);
        rowdata = worksheet2.createRow(row);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellValue(keyName);
        
        row+=2;
//        rowdata = worksheet2.createRow(row);
//        row++;
        col = 0;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categorycode");
        else
        keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.categorycode",null);
        rowdata = worksheet2.createRow(row);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryname");
        else
        keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.categoryname",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++; 
        // for Zebra and Tango by Sanjeew date 09/07/07
        // For Low balance alert allow
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.lowbalalertallow");
        else
        keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.lowbalalertallow",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        // End Zebra and Tango

        if(!BTSLUtil.isNullObject(p_messages))      
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grphdomaintype");
        else
        keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.grphdomaintype",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+1)));
        row++;
        rowdata = worksheet2.createRow(row); //next row
//        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grphdomaincode");
        else
        keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.grphdomaincode",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col)); //new column in the new row, just below the above column 
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grphdomainname");
        else
        keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.grphdomainname",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        rowdata = worksheet2.getRow(row-1); //going back to the previous row
        if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
	        if(!BTSLUtil.isNullObject(p_messages))
	        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.transfercontrolprf");
        	else
        		keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.transfercontrolprf",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            worksheet2.addMergedRegion(new CellRangeAddress(row-1, row-1, col, (col+1)));
//            col++;
            rowdata = worksheet2.getRow(row);
            if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.transfercontrolprfcode");
            else
            	keyName = RestAPIStringParser.getMessage(p_locale ,"batchusercreation.mastersheet.transfercontrolprfcode",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            col++;
            if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.transfercontrolprfname");
            else
            keyName = RestAPIStringParser.getMessage( p_locale,"batchusercreation.mastersheet.transfercontrolprfname",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            col++;
            rowdata = worksheet2.getRow(row-1);//going back to the previous row
        }

        if(!BTSLUtil.isNullObject(p_messages))     
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grouprole");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.grouprole",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        worksheet2.addMergedRegion(new CellRangeAddress(row-1, row-1, col, (col+1)));
//        col++;
        rowdata = worksheet2.getRow(row);
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grouprolecode");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.grouprolecode",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))        
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grouprolename");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.grouprolename",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        row++;
        
        col = 0;

        // Iteration Starts from Row number
        ArrayList categoryList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_LIST);
        ArrayList geographyList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GEOGRAPHY_TYPE_LIST);
        ArrayList gradeList = null;
        ArrayList transferProfileList = null;
        ArrayList commPrfList = null;
        if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
            gradeList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GRADE_LIST);
            transferProfileList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST);
            commPrfList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_COMMISION_PRF_LIST);
        }
        ArrayList grpRoleList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GROUP_ROLE_LIST);

        int catSize = 0;
        int tempCol = 0;
        int tempRow = 0;
        // Change for Batch User Initiate by Channel users
        int colIncrement = 0;
        int geoListSize = 0;
        int gradeListSize = 0;
        int trfPrfRowSize, transferPrfListSize = 0;
        int commPrfListSize = 0;
        int groupListSize = 0;
        int maxRow = 0;

        CategoryVO categoryVO = null;
        GeographicalDomainTypeVO geographicalDomainTypeVO = null;
        TransferProfileVO profileVO = null;
        UserRolesVO rolesVO = null;

        // Equate The Sizes of the list first so that it will not be checked in
        // the loop
        if (geographyList != null) {
            geoListSize = geographyList.size();
        }
        if (gradeList != null) {
            gradeListSize = gradeList.size();
        }
        if (transferProfileList != null) {
            transferPrfListSize = transferProfileList.size();
        }
        if (commPrfList != null) {
            commPrfListSize = commPrfList.size();
        }
        if (grpRoleList != null) {
            groupListSize = grpRoleList.size();
        }

        if (categoryList != null && (catSize = categoryList.size()) > 0) {
            for (int i = 0; i < catSize; i++) // Prints One Row Of Category
            {
            	rowdata = worksheet2.createRow(row);
                col = 0;
                colIncrement = 0;
                categoryVO = (CategoryVO) categoryList.get(i);
               
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(categoryVO.getCategoryCode());
                col++;
                
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(categoryVO.getCategoryName());
                col++;
               
                // for Zebra and Tango by Sanjeew date 09/07/07
                // For Low balance alert allow
               
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(categoryVO.getLowBalAlertAllow());
//                col++;
                // End Zebra and Tango

               
                // Now iterate Geographical domain type
                tempRow = row;
                maxrow = row;
                for (int j = 0; j < geoListSize; j++) {
                    tempCol = col;
                    
                    geographicalDomainTypeVO = (GeographicalDomainTypeVO) geographyList.get(j);
                    if (geographicalDomainTypeVO.getCategoryCode().equals(categoryVO.getCategoryCode())) {
                    	
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(++tempCol));
                        cell.setCellValue(geographicalDomainTypeVO.getGrphDomainType());
                        
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort( ++tempCol));
                        cell.setCellValue(geographicalDomainTypeVO.getGrphDomainTypeName());
                        tempRow++;
                        
                        
                    }
                }

                maxrow = --tempRow;
                tempRow = row;
                rowdata = worksheet2.getRow(row);
                if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {

                    // Now maxRow will contains the greatest value among the two
                    colIncrement += 2;
                    for (int l = 0; l < transferPrfListSize; l++) {
                        tempCol = col + colIncrement;
                        
                        profileVO = (TransferProfileVO) transferProfileList.get(l);
                        if (profileVO.getCategory().equals(categoryVO.getCategoryCode())) {
                        	cell = rowdata.createCell(BTSLUtil.parseIntToShort(++tempCol));
                            cell.setCellValue(profileVO.getProfileId());
                            
                           
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort(++tempCol));
                            cell.setCellValue(profileVO.getProfileName());
                            
                            tempRow++;
                            rowdata = worksheet2.createRow(tempRow);
                          }
                    }
                    maxrow = --tempRow;
                    trfPrfRowSize = --tempRow;
                    tempRow = row;

                    if (trfPrfRowSize > maxRow) {
                        maxRow = trfPrfRowSize;
                    }

                    colIncrement += 2;
                    rowdata = worksheet2.getRow(row);
                    for (int n = 0; n < groupListSize; n++) {
                        tempCol = col + colIncrement;
                        
                        rolesVO = (UserRolesVO) grpRoleList.get(n);
                        if (rolesVO.getCategoryCode().equals(categoryVO.getCategoryCode())) {
                        	cell = rowdata.createCell(BTSLUtil.parseIntToShort(++tempCol));
                            cell.setCellValue(rolesVO.getRoleCode());
                          
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort( ++tempCol));
                            cell.setCellValue(rolesVO.getRoleName());
                            tempRow++;
                            
                            rowdata = worksheet2.getRow(tempRow);
                            if(rowdata==null) {
                            	rowdata = worksheet2.createRow(tempRow);	
                            }
                        }
                    }  

                    maxrow = tempRow;
                    if (tempRow > maxRow) {
                        maxRow = tempRow;
                    }
                    
                    
                    // Max size of ROWS according to the data
                    row = maxRow+1;
                    colIncrement = 0;
                }
            }

        }
        return maxrow;
    }

/**
 * @param worksheet2
 * @param col
 * @param row
 * @param p_hashMap
 * @param cellStyle
 * @return
 * @throws Exception
 */
private int writeGeographyListing(SXSSFSheet  worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) {
    if (_log.isDebugEnabled()) {
        _log.debug("writeGeographyListingForUpdate", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
    }
    final String METHOD_NAME = "writeGeographyListingForUpdate";
    Row rowdata = null;
    Cell cell = null;
    String keyName = null;
    try {
    	if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.avaliablegeographieslist");
    	else
    	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.avaliablegeographieslist",null);
        rowdata = worksheet2.createRow(row);
        //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+2)));
        row++;
        col = 0;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.avaliablegeographieslist.note");
        else
        keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.avaliablegeographieslist.note",null);
        rowdata = worksheet2.createRow(row);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
       	cell.setCellValue(keyName);
       	worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (COLUMN_MARGE)));
        row++;
        rowdata = worksheet2.createRow(row);
        col = 0;
        // Logic for generating headings
        ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GEOGRAPHY_LIST);
        UserGeographiesVO userGeographiesVO = null;
        ArrayList geoDomainTypeList = new ArrayList();

        if (list != null) {
            for (int i = 0, j = list.size(); i < j; i++) {
                userGeographiesVO = (UserGeographiesVO) list.get(i);
                if (!geoDomainTypeList.contains(userGeographiesVO.getGraphDomainType())) {
                    geoDomainTypeList.add(userGeographiesVO.getGraphDomainType());
                }
            }
        }
        String endTagCode = null;
        String endTagName = null;
        // Generate Headings from the ArrayList
        if(!BTSLUtil.isNullObject(p_messages))
         endTagCode = p_messages.getMessage(p_locale, "code");
        else
         endTagCode = RestAPIStringParser.getMessage(p_locale, "code",null);
        
        if(!BTSLUtil.isNullObject(p_messages))
         endTagName = p_messages.getMessage(p_locale, "name");
        else
         endTagName = RestAPIStringParser.getMessage(p_locale, "name",null);
        String geoType = null;
        col = 0;
        for (int i = 0, j = geoDomainTypeList.size(); i < j; i++) {
            geoType = ((String) geoDomainTypeList.get(i)).trim();
			if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, geoType);
            else
            keyName = PretupsRestUtil.getMessageString( geoType);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName + " " + endTagCode + "(" + geoType + ")");
            col++;
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName + " " + endTagName);
            col++;
        }
        row++;
        rowdata = worksheet2.createRow(row);
        col = 0;
        int nameOccurance = 0;
        int oldseqNo = 0;
        int sequence_num = 0;
        if (list != null) {
            sequence_num = ((UserGeographiesVO) list.get(0)).getGraphDomainSequenceNumber();
            for (int i = 0, j = list.size(); i < j; i++) {

                userGeographiesVO = (UserGeographiesVO) list.get(i);
                if (oldseqNo > userGeographiesVO.getGraphDomainSequenceNumber()) {
                    nameOccurance -= (oldseqNo - userGeographiesVO.getGraphDomainSequenceNumber()); 
                } else if (oldseqNo < userGeographiesVO.getGraphDomainSequenceNumber()) {
                    nameOccurance++;
                }

                col = nameOccurance + userGeographiesVO.getGraphDomainSequenceNumber() - sequence_num;
                // Change made for batch user creation by channel user
                if (userGeographiesVO.getGraphDomainSequenceNumber() == sequence_num) {
                    col = 0;
                    nameOccurance = 0;
                }
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
               	cell.setCellValue(userGeographiesVO.getGraphDomainCode());
               	
               	cell = rowdata.createCell( BTSLUtil.parseIntToShort((col+1)) );
               	cell.setCellValue(userGeographiesVO.getGraphDomainName());
               	
                oldseqNo = userGeographiesVO.getGraphDomainSequenceNumber();
                row++;
                rowdata = worksheet2.createRow(row);
            }
        }

        return row;
    } catch (Exception e) {
        _log.errorTrace(METHOD_NAME, e);
        _log.error("writeGeographyListingForUpdate", " Exception e: " + e.getMessage());
    }finally {
    	if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting");
        }
    }
	return row;
}
    
/**
 * @param worksheet2
 * @param col
 * @param row
 * @param p_hashMap
 * @param cellStyle
 * @return
 * @throws Exception
 */
private int writeCategoryHierarchy(SXSSFSheet worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) {
	 final String METHOD_NAME = "writeCategoryHierarchy";
	if (_log.isDebugEnabled()) {
        _log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
    }
    Row rowdata = null;
    Cell cell = null;
    String keyName = null;
    try {
    	if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryhierarchy");
    	else
    	keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.categoryhierarchy",null);
        rowdata = worksheet2.createRow(row);
        //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+2)));

        row++;
        col = 0;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryhierarchy.note");
        else
        keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.categoryhierarchy.note",null);
        rowdata = worksheet2.createRow(row);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        worksheet2.addMergedRegion(new CellRangeAddress(row,row, col, COLUMN_MARGE));
        row++;
        col = 0;
        
        rowdata = worksheet2.createRow(row);
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.parentcategory");
        else
        keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.parentcategory",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.childcategory");
		else
        keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.childcategory",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        row++;
        col = 0;

        ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_HIERARCHY_LIST);
        ChannelTransferRuleVO channelTransferRuleVO = null;
        int oldSeqNum = 0;
        if (list != null) {
            for (int i = 0, j = list.size(); i < j; i++) {
                col = 0;
                channelTransferRuleVO = (ChannelTransferRuleVO) list.get(i);
                rowdata = worksheet2.createRow(row);
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                // Parent Category will be displayed according to Seq Num
                if (i == 0 || oldSeqNum != channelTransferRuleVO.getFromSeqNo()) {
                	cell.setCellValue(channelTransferRuleVO.getFromCategory());
                } else {
                	cell.setCellValue("");
                }
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellValue(channelTransferRuleVO.getToCategory());
                oldSeqNum = channelTransferRuleVO.getFromSeqNo();
                row++;
            }
        }
        return row;
    } catch (Exception e) {
        _log.errorTrace(METHOD_NAME, e);
        _log.error(METHOD_NAME, " Exception e: " + e.getMessage());
    } finally {
    	if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting");
        }
    }
	return row;

}

/**
 * Method writeCategoryData
 * This method writes the category details including
 * geographies,grade,commision profile,transfer
 * profile & group information
 * 
 * @param worksheet2
 * @param col
 * @param row
 * @param p_hashMap
 * @return
 * @throws Exception
 */
/*
private int writeCategoryData(SXSSFSheet worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) throws Exception {
    int maxrow = row;
    if (_log.isDebugEnabled()) {
        _log.debug("writeCategoryData", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
    }
    Row rowdata = null;
    Cell cell = null;
    String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryheading");
    rowdata = worksheet2.createRow(row);
    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    cell.setCellStyle(cellStyle);
    cell.setCellValue(keyName);
    worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+2)));
    row++;
    col = 0;
    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryheading.note");
    rowdata = worksheet2.createRow(row);
    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    cell.setCellStyle(cellStyle);
    cell.setCellValue(keyName);
    worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, COLUMN_MARGE));
    row++;
    col = 0;
    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categorycode");
    rowdata = worksheet2.createRow(row);
    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    cell.setCellStyle(cellStyle);
    cell.setCellValue(keyName);
    col ++ ;
    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryname");
    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    cell.setCellStyle(cellStyle);
    cell.setCellValue(keyName);

    // for Zebra and Tango by Sanjeew date 09/07/07
    // For Low balance alert allow
    col++;
    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.lowbalalertallow");
    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    cell.setCellStyle(cellStyle);
    cell.setCellValue(keyName);
    // End Zebra and Tango

    
    col++;
    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grphdomaintype");
    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    cell.setCellStyle(cellStyle);
    cell.setCellValue(keyName);
    worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, col + 1));

    row++;
    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grphdomaincode");
    rowdata = worksheet2.createRow(row);
    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    cell.setCellStyle(cellStyle);
    cell.setCellValue(keyName);
    col++;
    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grphdomainname");
    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    cell.setCellStyle(cellStyle);
    cell.setCellValue(keyName);

    row--;// going back to the earlier row
    rowdata = worksheet2.getRow(row);
    if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
    	col++;
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.transfercontrolprf");
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, col + 1));
        row++;
        rowdata = worksheet2.getRow(row);
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.transfercontrolprfcode");
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.transfercontrolprfname");
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
    }
    row--;//going back to the earlier row
    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grouprole");
    label = new Label(++col, row, keyName, times16format);
    worksheet2.mergeCells(col, row, col + 1, row);
    worksheet2.addCell(label);
    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grouprolecode");
    label = new Label(col, row + 1, keyName, times16format);
    worksheet2.addCell(label);
    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grouprolename");
    label = new Label(++col, row + 1, keyName, times16format);
    worksheet2.addCell(label);

    row++;
    col = 0;

    // Iteration Starts from Row number
    row++;
    ArrayList categoryList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_LIST);
    ArrayList geographyList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GEOGRAPHY_TYPE_LIST);
    ArrayList gradeList = null;
    ArrayList transferProfileList = null;
    ArrayList commPrfList = null;
    if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
        gradeList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GRADE_LIST);
        transferProfileList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST);
        commPrfList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_COMMISION_PRF_LIST);
    }
    ArrayList grpRoleList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GROUP_ROLE_LIST);

    int catSize = 0;
    int tempCol = 0;
    int tempRow = 0;
    // Change for Batch User Initiate by Channel users
    int colIncrement = 0;
    int geoListSize = 0;
    int gradeListSize = 0;
    int trfPrfRowSize, transferPrfListSize = 0;
    int commPrfListSize = 0;
    int groupListSize = 0;
    int maxRow = 0;

    CategoryVO categoryVO = null;
    GeographicalDomainTypeVO geographicalDomainTypeVO = null;
    TransferProfileVO profileVO = null;
    UserRolesVO rolesVO = null;

    // Equate The Sizes of the list first so that it will not be checked in
    // the loop
    if (geographyList != null) {
        geoListSize = geographyList.size();
    }
    if (gradeList != null) {
        gradeListSize = gradeList.size();
    }
    if (transferProfileList != null) {
        transferPrfListSize = transferProfileList.size();
    }
    if (commPrfList != null) {
        commPrfListSize = commPrfList.size();
    }
    if (grpRoleList != null) {
        groupListSize = grpRoleList.size();
    }

    if (categoryList != null && (catSize = categoryList.size()) > 0) {
        for (int i = 0; i < catSize; i++) // Prints One Row Of Category
        {
            col = 0;
            colIncrement = 0;
            categoryVO = (CategoryVO) categoryList.get(i);
            label = new Label(col, row, categoryVO.getCategoryCode());
            worksheet2.addCell(label);
            label = new Label(++col, row, categoryVO.getCategoryName());
            worksheet2.addCell(label);

            // for Zebra and Tango by Sanjeew date 09/07/07
            // For Low balance alert allow
            label = new Label(++col, row, categoryVO.getLowBalAlertAllow());
            worksheet2.addCell(label);
            // End Zebra and Tango

            
            // Now iterate Geographical domain type
            tempRow = row;
            maxrow = row;
            for (int j = 0; j < geoListSize; j++) {
                tempCol = col;
                geographicalDomainTypeVO = (GeographicalDomainTypeVO) geographyList.get(j);
                if (geographicalDomainTypeVO.getCategoryCode().equals(categoryVO.getCategoryCode())) {
                    label = new Label(++tempCol, tempRow, geographicalDomainTypeVO.getGrphDomainType());
                    worksheet2.addCell(label);
                    label = new Label(++tempCol, tempRow, geographicalDomainTypeVO.getGrphDomainTypeName());
                    worksheet2.addCell(label);
                    tempRow++;
                }
            }

            maxrow = tempRow;
            tempRow = row;

            if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {

                // Now maxRow will contains the greatest value among the two
                colIncrement += 2;
                for (int l = 0; l < transferPrfListSize; l++) {
                    tempCol = col + colIncrement;
                    profileVO = (TransferProfileVO) transferProfileList.get(l);
                    if (profileVO.getCategory().equals(categoryVO.getCategoryCode())) {
                        label = new Label(++tempCol, tempRow, profileVO.getProfileId());
                        worksheet2.addCell(label);
                        label = new Label(++tempCol, tempRow, profileVO.getProfileName());
                        worksheet2.addCell(label);
                        tempRow++;
                    }
                }
                maxrow = tempRow;
                trfPrfRowSize = tempRow;
                tempRow = row;

                if (trfPrfRowSize > maxRow) {
                    maxRow = trfPrfRowSize;
                }

                colIncrement += 2;

                for (int n = 0; n < groupListSize; n++) {
                    tempCol = col + colIncrement;
                    rolesVO = (UserRolesVO) grpRoleList.get(n);
                    if (rolesVO.getCategoryCode().equals(categoryVO.getCategoryCode())) {
                        label = new Label(++tempCol, tempRow, rolesVO.getRoleCode());
                        worksheet2.addCell(label);
                        label = new Label(++tempCol, tempRow, rolesVO.getRoleName());
                        worksheet2.addCell(label);
                        tempRow++;
                    }
                }

                maxrow = tempRow;
                if (tempRow > maxRow) {
                    maxRow = tempRow;
                }
                // Max size of ROWS according to the data
                row = maxRow;
                colIncrement = 0;
            }
        }

    }
    return maxrow;
}
*/
/**
 * @param worksheet2
 * @param col
 * @param row
 * @param p_hashMap
 * @param style 
 * @return
 * @throws Exception
 */
/*
private int writeCommissionData(SXSSFSheet worksheet2, int col, int row, HashMap p_hashMap, CellStyle style) throws Exception {
    row += 2; // to give some space before writing the next records
    if (_log.isDebugEnabled()) {
        _log.debug("writeCommissionData", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
    }
    String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.commissionheading");
    Label label = new Label(col, row, keyName, times16format);
    worksheet2.mergeCells(col, row, col + 2, row);
    worksheet2.addCell(label);
    row++;
    col = 0;
    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.commissionheading.note");
    label = new Label(col, row, keyName);
    worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
    worksheet2.addCell(label);
    row++;
    col = 0;
    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categorycode");
    label = new Label(col, row, keyName, times16format);
    worksheet2.addCell(label);
    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryname");
    label = new Label(++col, row, keyName, times16format);
    worksheet2.addCell(label);

    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grphcode");
    label = new Label(++col, row, keyName, times16format);
    worksheet2.addCell(label);
    keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grphname");
    label = new Label(++col, row, keyName, times16format);
    worksheet2.addCell(label);

    if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grade");
        label = new Label(++col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 1, row);
        worksheet2.addCell(label);

        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.gradecode");
        label = new Label(col, row + 1, keyName, times16format);
        worksheet2.addCell(label);
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.gradename");
        label = new Label(++col, row + 1, keyName, times16format);
        worksheet2.addCell(label);

        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.commisionprofile");
        label = new Label(++col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 1, row);
        worksheet2.addCell(label);
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.commisionprofilecode");
        label = new Label(col, row + 1, keyName, times16format);
        worksheet2.addCell(label);
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.commisionprofilename");
        label = new Label(++col, row + 1, keyName, times16format);
        worksheet2.addCell(label);
    }

    row++;
    col = 0;

    // Iteration Starts from Row number
    row = row + 1;
    ArrayList commProfList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_COMM_LIST);

    int catSize = 0;
    // Equate The Sizes of the list first so that it will not be checked in
    // the loop
    CommissionProfileSetVO CSVO = null;

    if (commProfList != null && (catSize = commProfList.size()) > 0) {
        for (int i = 0; i < catSize; i++) // Prints One Row Of Category
        {
            col = 0;

            CSVO = (CommissionProfileSetVO) commProfList.get(i);
            label = new Label(col, row, CSVO.getCategoryCode());
            worksheet2.addCell(label);
            label = new Label(++col, row, CSVO.getCategoryName());
            worksheet2.addCell(label);
            label = new Label(++col, row, CSVO.getGrphDomainCode());
            worksheet2.addCell(label);
            label = new Label(++col, row, CSVO.getGrphDomainName());
            worksheet2.addCell(label);
            label = new Label(++col, row, CSVO.getGradeCode());
            worksheet2.addCell(label);
            label = new Label(++col, row, CSVO.getGradeName());
            worksheet2.addCell(label);
            label = new Label(++col, row, CSVO.getCommProfileSetId());
            worksheet2.addCell(label);
            label = new Label(++col, row, CSVO.getCommProfileSetName());
            worksheet2.addCell(label);
            row++;
        }
    }
    return row;

}
*/

/**
 * @param worksheet2
 * @param col
 * @param row
 * @param p_hashMap
 * @return
 * @throws Exception
 */
private int writeMpayProfID(SXSSFSheet worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) {
    final String METHOD_NAME = "writeMpayProfID";
	if (_log.isDebugEnabled()) {
        _log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
    }
    Row rowdata = null;
    Cell cell = null;
    String keyName = null;
    try {
    	if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.mpayprofiledetails");
    	else
    		keyName = RestAPIStringParser.getMessage(p_locale,"batchusercreation.mastersheet.mpayprofiledetails",null);
        rowdata = worksheet2.createRow(row);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (col+2)));

        row++;
        col = 0;
        if(!BTSLUtil.isNullObject(p_messages))	
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.gradecode");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale,"batchusercreation.mastersheet.gradecode",null);
        rowdata = worksheet2.createRow(row);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))	
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.mpayprofileid");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale,"batchusercreation.mastersheet.mpayprofileid",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))	
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.mpayprofileiddesc");
		else
        keyName = RestAPIStringParser.getMessage(p_locale,"batchusercreation.mastersheet.mpayprofileiddesc",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);


        row++;
        col = 0;
        ArrayList list = (ArrayList) p_hashMap.get(PretupsI.M_PAY_PROFILE_LIST);
        ListValueVO listValueVO = null;
        String strvalue = null;
        String mpayIDName = null;
        String usrGrd = null;
        ArrayList usrGrdList = new ArrayList();
        
        if (list != null) {
            for (int i = 0, j = list.size(); i < j; i++) {
            	rowdata = worksheet2.createRow(row);
                col = 0;
                listValueVO = (ListValueVO) list.get(i);
                mpayIDName = listValueVO.getLabel();
                strvalue = listValueVO.getValue();
                usrGrd = strvalue.split(":")[1];
                if (usrGrdList.size() > 0 && usrGrdList.contains(usrGrd)) {
                    ++col;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(strvalue.split(":")[2]);
                    ++col;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(mpayIDName);
                } else {
                    usrGrdList.add(usrGrd);
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(usrGrd);
                    ++col;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(strvalue.split(":")[2]);
                    ++col;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(mpayIDName);
                }
                row++;
            }
        }
        return row;
    } catch (Exception e) {
        _log.errorTrace(METHOD_NAME, e);
        _log.error("writeGeographyListingForUpdate", " Exception e: " + e.getMessage());
    }finally {
    	if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting");
        }
    }
    return row;
}



/**
 * @param worksheet1
 * @param col
 * @param row
 * @param p_hashMap
 * @throws Exception
 */
private void writeInDataSheet(SXSSFSheet worksheet1, int col, int row, HashMap p_hashMap, CellStyle cellStyle) {
	final String METHOD_NAME = "writeInDataSheet";
	if (_log.isDebugEnabled()) {
        _log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
    }
	boolean ptupsMobqutyMergd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD);
	boolean isFnameLnameAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED);
	boolean externalCodeMandatoryForUser = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORUSER);
	boolean autoPinGenerateAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PIN_GENERATE_ALLOW);
	boolean userVoucherTypeAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
	boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
	boolean loginPasswordAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED);
	String userCreationMandatoryFields = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_CREATION_MANDATORY_FIELDS);
	boolean batchUserProfileAssign = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN);
	boolean rsaAuthenticationRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RSA_AUTHENTICATION_REQUIRED);
	boolean authTypeReq = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTH_TYPE_REQ);

	Row rowdata = null;
    Cell cell = null;
    String keyName = null;
    try {
    	if(!BTSLUtil.isNullObject(p_messages))
        keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.initiate.heading", new String[]{(String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME)});
    	else
    		keyName = RestAPIStringParser.getMessage( p_locale,"bulkuser.xlsfile.initiate.heading", new String[] {(String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME)});
        String comment = null;
        rowdata = worksheet1.createRow(row);
        //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        worksheet1.addMergedRegion(new CellRangeAddress(row, row, col, (col+10)));

        row++;
        col = 0;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.header.downloadedby");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.header.downloadedby",null);
        rowdata = worksheet1.createRow(row);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        ++col;
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue((String) p_hashMap.get(PretupsI.BATCH_USR_CREATED_BY));
        row++;
        col = 0;
        rowdata = worksheet1.createRow(row);
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.header.domainname");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.header.domainname",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        ++col;
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellValue((String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME));

        row++;
        col = 0;
        rowdata = worksheet1.createRow(row);
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.header.geographyname");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.header.geographyname",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        ++col;
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellValue((String) p_hashMap.get(PretupsI.BATCH_USR_GEOGRAPHY_NAME));
        row++;
        col = 0;
        ++col;
        rowdata = worksheet1.createRow(row);
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.strar");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.strar",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellValue(keyName);
        worksheet1.addMergedRegion(new CellRangeAddress(row, row, col, (col+5)));
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.doublestrar");
        else
        	 keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.doublestrar",null);
        col+=6;
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellValue(keyName);
        worksheet1.addMergedRegion(new CellRangeAddress(row, row, col, (col+15)));

        row++;
        col = 0;
        rowdata = worksheet1.createRow(row);
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.parentmsisdn");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.parentmsisdn",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.parentmsisdn.comment"));
        else
        	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.parentmsisdn.comment",null));
        col++;

         if(!BTSLUtil.isNullString(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES"))){
        	if(PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES")))
			    {
        		if(!BTSLUtil.isNullObject(p_messages))
				keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.usernameprefixnotmandatory");
				else
        			keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.usernameprefixnotmandatory",null);
        		}
				else
				{
					if(!BTSLUtil.isNullObject(p_messages))
			        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.usernameprefix");
					else
						keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.usernameprefix",null);
			     }
	 }
        else{
			if(!BTSLUtil.isNullObject(p_messages))
        	keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.usernameprefix");
        	else
        		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.usernameprefix",null);
        }
         cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
         cell.setCellStyle(cellStyle);
         cell.setCellValue(keyName);
         if(!BTSLUtil.isNullObject(p_messages))			
         setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.usernameprefix.comment"));
         else
        	 setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.usernameprefix.comment",null));
         col++;
        if (!isFnameLnameAllowed) {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.username");
        	else
        		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.username",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            if(!BTSLUtil.isNullObject(p_messages))
            setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.username.comment"));
            else
            	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.username.comment",null));
        } else {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.firstname");
        	else
        		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.firstname",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            if(!BTSLUtil.isNullObject(p_messages))
            setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.firstname.comment"));
            else
            	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.firstname.comment",null));
            col++;
            if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.lastname");
            else
            	keyName = RestAPIStringParser.getMessage( p_locale,"bulkuser.xlsfile.details.lastname",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            if(!BTSLUtil.isNullObject(p_messages))
            setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.firstname.comment"));
            else
            	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.firstname.comment",null));
        }
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.shortname");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.shortname",null);
        if(!(BTSLUtil.isNullString(Constants.getProperty("SECURITY_QUESTION_FIELD"))) && Constants.getProperty("SECURITY_QUESTION_FIELD").equals("SHORT_NAME"))
        {
        	keyName = keyName + "*";
        }
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.categorycode");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.categorycode",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.categorycode.comment"));
        else
        	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.categorycode.comment",null));
        col++;
        if (externalCodeMandatoryForUser) {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.externalcode.star");
        	else
        		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.externalcode.star",null);
        } else {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.externalcode");
        	else
        		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.externalcode",null);
        }
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
		if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.contactperson");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.contactperson",null);
        if(!(BTSLUtil.isNullString(Constants.getProperty("SECURITY_QUESTION_FIELD"))) && Constants.getProperty("SECURITY_QUESTION_FIELD").equals("CONTACT_PERSON"))
        {
        	keyName = keyName + "*";
        }
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.address1");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.address1",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.city");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.city",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.state");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.state",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.ssn");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.ssn",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.ssn.comment"));
        else
        	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.ssn.comment",null));
        col++;

        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.country");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.country",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;

        // Added by deepika aggarwal
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.company");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.company",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.fax");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.fax",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;

        if (BTSLUtil.isStringContain(userCreationMandatoryFields, "email")) {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.email.star");
        	else
        		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.email.star",null);
        } else {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.email");
        	else
        		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.email",null);
        }
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        // ***********************Added By Deepika Aggarwal**************
        if(!BTSLUtil.isNullString(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES"))){
        	if(PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES")))
        		{
        		if(!BTSLUtil.isNullObject(p_messages))
        		keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.languagenotmandatory");
        		else
        			keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.languagenotmandatory",null);
        		}
        	else
        		{
        		if(!BTSLUtil.isNullObject(p_messages))
        		keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.language");
        		else
        			keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.language",null);
        		}
        }
        else{
        	if(!BTSLUtil.isNullObject(p_messages))
        	keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.language");
        	else
        		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.language",null);
        }       
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.language.comment"));
        else
        setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.language.comment",null));
        col++;

        // end added by deepika aggarwal

        if (loginPasswordAllowed) {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.loginid");
            else
            	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.loginid",null);
            	
        } else {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.loginid.new");
        	else
        		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.loginid.new",null);
        }
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.loginid.comment"));
        else
        	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.loginid.comment",null));
        col++;

       /* // Diwakar on 03-MAY-2014 OCM
        if (loginPasswordAllowed) {
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.password.star");
        } else {
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.password");
        }*/
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.password");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.password",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.mobilenumber");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.mobilenumber",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.mobilenumberprisec.comment"));
        else
        	setCellComment(cell,RestAPIStringParser.getMessage( p_locale,"bulkuser.xlsfile.details.mobilenumberprisec.comment",null));
        col++;

        // Diwakar on 03-MAY-2014 OCM
        if (!autoPinGenerateAllow	) {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.pin.star");
        	else
        		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.pin.star",null);
        } else {
        	if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.pin");
        	else
        		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.pin",null);
        }
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.geographycode");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.geographycode",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.geographycode.comment"));
        else
        	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.geographycode.comment",null));
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.grouprolecode");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.grouprolecode",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.grouprolecode.comment"));
        else
        	setCellComment(cell,RestAPIStringParser.getMessage( p_locale,"bulkuser.xlsfile.details.grouprolecode.comment",null));
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.services");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.services",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.services.comment"));
        else
        	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.services.comment",null));
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.outlet");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.outlet",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.outlet.comment"));
        else
        	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.outlet.comment",null));
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.suboutletcode");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.suboutletcode",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.suboutletcode.comment"));
        else
        	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.suboutletcode.comment",null));
        col++;
        if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
        	if(!BTSLUtil.isNullObject(p_messages))
        	keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.commisionprofile");
        	else
        		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.commisionprofile",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            if(!BTSLUtil.isNullObject(p_messages))
            setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.commisionprofile.comment"));
            else
            	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.commisionprofile.comment",null));
            col++;
            if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.transferprofile");
            else
            	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.transferprofile",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            if(!BTSLUtil.isNullObject(p_messages))
            setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.transferprofile.comment"));
            else
            	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.transferprofile.comment",null));
            col++;

            // for Zebra and Tango by Sanjeew date 09/07/07
            if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.grade");
            else
            	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.grade",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(keyName);
            if(!BTSLUtil.isNullObject(p_messages))
            setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.grade.comment"));
            else
            	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.grade.comment",null));
            col++;

            if (ptupsMobqutyMergd) {
                if(!BTSLUtil.isNullObject(p_messages))
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.mcomorceflag");
            	else
            		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.mcomorceflag",null);
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellStyle(cellStyle);
                cell.setCellValue(keyName);
                if(!BTSLUtil.isNullObject(p_messages))
                setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.mcomorceflag.comment"));
                else
                setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.mcomorceflag.comment",null));
                col++;
                if(!BTSLUtil.isNullObject(p_messages))
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.mpayprofileid");
                else
                	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.mpayprofileid",null);
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellStyle(cellStyle);
                cell.setCellValue(keyName);
                if(!BTSLUtil.isNullObject(p_messages))
                setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.mpayprofileid.comment"));
                else
                	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.mpayprofileid.comment",null));
                                col++;
            }

            if (isTrfRuleUserLevelAllow) {
                if(!BTSLUtil.isNullObject(p_messages))
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.trfruletypecode");
            	else
            		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.trfruletypecode",null);
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellStyle(cellStyle);
                cell.setCellValue(keyName);
                if(!BTSLUtil.isNullObject(p_messages))
                setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.trfruletypecode.comment"));
                else
                	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.trfruletypecode.comment",null));
                col++;
            }

            if (rsaAuthenticationRequired) {
                // For RSA Authentication
                if(!BTSLUtil.isNullObject(p_messages))
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.rsaauthentication");
            	else
            		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.rsaauthentication",null);
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellStyle(cellStyle);
                cell.setCellValue(keyName);
                if(!BTSLUtil.isNullObject(p_messages))
                setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.rsaauthentication.comment"));
                else
                	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.rsaauthentication.comment",null));
                col++;
            }
            if (authTypeReq) {
                if(!BTSLUtil.isNullObject(p_messages))
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.authtypeallowed");
            	else
            		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.authtypeallowed",null);
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellStyle(cellStyle);
                cell.setCellValue(keyName);
                if(!BTSLUtil.isNullObject(p_messages))
                setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.authtypeallowed.comment"));
                else
                	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.authtypeallowed.comment",null));
                col++;
            }

        }
if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.lowbalalertallow");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.lowbalalertallow",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.lowbalalertallow.comment"));
        else
        	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.lowbalalertallow.comment",null));
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.longitude");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.longitude",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.longitude.comment"));
        else
        setCellComment(cell,RestAPIStringParser.getMessage(p_locale , "bulkuser.xlsfile.details.longitude.comment",null));
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.latitude");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale,   "bulkuser.xlsfile.details.latitude",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.latitude.comment"));
        else
        	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.latitude.comment",null));
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.documenttype");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.documenttype",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.documenttype.comment"));
        else
        	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.documenttype.comment",null));
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.documentno");
        else
        	keyName = RestAPIStringParser.getMessage(  p_locale, "bulkuser.xlsfile.details.documentno",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.documentno.comment"));
        else
        	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.documentno.comment",null));
        col++;
        if(!BTSLUtil.isNullObject(p_messages))
        keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.paymenttype");
        else
        	keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.paymenttype",null);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        if(!BTSLUtil.isNullObject(p_messages))
        setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.paymenttype.comment"));
        else
        	setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.paymenttype.comment",null));
        col++;
        
        if(userVoucherTypeAllowed){
			if(!BTSLUtil.isNullObject(p_messages))
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.vouchertype");    
        	else
        		keyName = RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.vouchertype",null);
            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			if(!BTSLUtil.isNullObject(p_messages))
			setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.vouchertype.comment"));
			else
				setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "bulkuser.xlsfile.details.vouchertype.comment",null));
           }
        col++;
        
        keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.loanprofilecode",null);
        cell = rowdata.createCell((short) col);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(keyName);
        setCellComment(cell,RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.loanprofilecode.comment",null));
        
        col++;
        
        
    } catch (Exception e) {
        _log.errorTrace(METHOD_NAME, e);
        _log.error(METHOD_NAME, " Exception e: " + e.getMessage());
    }finally {
    	if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting");
        }
    }
}

public String[][] readMultipleExcelSheetforUserCreation(String p_excelID, String p_fileName, boolean p_readLastSheet, int p_leftHeaderLinesForEachSheet, HashMap<String, String> map) {
    if (_log.isDebugEnabled()) {
        _log.debug("readMultipleExcelSheet", " p_excelID: " + p_excelID + " p_fileName: " + p_fileName + " p_readLastSheet=" + p_readLastSheet, " p_leftHeaderLinesForEachSheet=" + p_leftHeaderLinesForEachSheet);
    }
    final String METHOD_NAME = "readMultipleExcelSheet";
    String strArr[][] = null;
    int arrRow = p_leftHeaderLinesForEachSheet;
    XSSFWorkbook workbook = null;
    XSSFSheet excelsheet = null;
    int noOfSheet = 0;
    int noOfRows = 0;
    int noOfcols = 0;
    InputStream fileInStream = null;
    try {
    	fileInStream = new FileInputStream(p_fileName);
        workbook = new XSSFWorkbook (fileInStream);
        noOfSheet = workbook.getNumberOfSheets();
        if (!p_readLastSheet) {
            noOfSheet = noOfSheet - 1;
        }
        // Total number of rows in the excel sheet
        for (int i = 0; i < noOfSheet; i++) {
            excelsheet = workbook.getSheetAt(i);
            noOfRows = noOfRows + (excelsheet.getLastRowNum()+1 - p_leftHeaderLinesForEachSheet);
            noOfcols = excelsheet.getRow(p_leftHeaderLinesForEachSheet-1).getLastCellNum();
        }
        // Initialization of string array
        strArr = new String[noOfRows + p_leftHeaderLinesForEachSheet][noOfcols];
        for (int i = 0; i < noOfSheet; i++) {
            excelsheet = workbook.getSheetAt(i);
            noOfRows = excelsheet.getLastRowNum()+1;
            noOfcols = excelsheet.getRow(p_leftHeaderLinesForEachSheet-1).getLastCellNum();
            
            int rowCount=0;
            XSSFRow row1; 
    		XSSFCell cell;
    		Iterator rows11 = excelsheet.rowIterator();
            String content = null;
            String key = null;
            int[] indexMapArray = new int[noOfcols];
            String indexStr = null;
            for (int k = 0; k < p_leftHeaderLinesForEachSheet; k++) {
                for (int col = 0; col < noOfcols; col++)

                {
                    indexStr = null;
                    key = ExcelFileConstants.getReadProperty(p_excelID, String.valueOf(col));
                    if (key == null) {
                        key = String.valueOf(col);
                    }
                    indexStr = ExcelFileConstants.getReadProperty(p_excelID, String.valueOf(col));
                    if (indexStr == null) {
                        indexStr = String.valueOf(col);
                    }
                    indexMapArray[col] = Integer.parseInt(indexStr);
                    // strArr[0][indexMapArray[col]] = key;
                    strArr[k][indexMapArray[col]] = key;
                }
            }
            while (rows11.hasNext())
    		{   
    			if(rowCount < p_leftHeaderLinesForEachSheet) {
    			rowCount++;
    			row1=(XSSFRow) rows11.next();
    			continue;
    		    }
    			map.put(Integer.toString(arrRow + 1), workbook.getSheetName(i) + PretupsI.ERROR_LINE + (rowCount + 1));
    			row1=(XSSFRow) rows11.next();
    			Iterator cells = row1.cellIterator();
    			int colCount = 0;
    			for(colCount=0; colCount < noOfcols; colCount++) {
    				
    				 if( row1.getCell(colCount) == null ){
                         cell = row1.createCell(colCount);
                     } else {
                         cell = row1.getCell(colCount);
                     }
                     cell.setCellType(CellType.STRING);
    				 content = cell.getStringCellValue();
                     content = content.replaceAll("\n", " ");
                     content = content.replaceAll("\r", " ");
                     strArr[arrRow][indexMapArray[colCount]] = content;
    			}
    			rowCount++;
    			arrRow++;
    			
    		}

        }
        return strArr;
    } catch (Exception e) {
        _log.errorTrace(METHOD_NAME, e);
        _log.error("readMultipleExcelSheet", " Exception e: " + e.getMessage());
    } finally {
        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        try {
            if (fileInStream != null) {
            	fileInStream.close();
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        workbook = null;
        excelsheet = null;
        if (_log.isDebugEnabled()) {
            _log.debug("readMultipleExcelSheet", " Exiting strArr: " + strArr);
        }
    }
    return strArr;
}

private int writeUserDocumentTypeList(SXSSFSheet  worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) {
	final String METHOD_NAME = "writeUserDocumentTypeList";
	if (_log.isDebugEnabled()) {
		_log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
	}
	Row rowdata = null;
	Cell cell = null;
	String keyName = null;
	try {
		if(!BTSLUtil.isNullObject(p_messages))
		keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.documenttypeheading");
		else
			keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.documenttypeheading",null);
		rowdata = worksheet2.createRow(row);
		//cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell.setCellStyle(cellStyle);
		cell.setCellValue(keyName);
		worksheet2.addMergedRegion(new CellRangeAddress(row,  row, col,(col+2)));
		row++;
		col = 0;
		rowdata = worksheet2.createRow(row);
		if(!BTSLUtil.isNullObject(p_messages))
		keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.documenttypeheading.note");
		else
			keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.documenttypeheading.note",null);
		//cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell.setCellValue(keyName);
		worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (COLUMN_MARGE)));
		row++;
		col = 0;

		rowdata = worksheet2.createRow(row);
		if(!BTSLUtil.isNullObject(p_messages))
		keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.documenttypecode");
		else
			keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.documenttypecode",null);
		cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell.setCellStyle(cellStyle);
		cell.setCellValue(keyName);
		col++;
		if(!BTSLUtil.isNullObject(p_messages))
		keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.documenttypename");
		else
			keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.documenttypename",null);
		cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell.setCellStyle(cellStyle);
		cell.setCellValue(keyName);
		row++;
		rowdata = worksheet2.createRow(row);
		col = 0;

		ArrayList list = (ArrayList) p_hashMap.get(PretupsI.USER_DOCUMENT_TYPE);
		ListValueVO listValueVO = null;
		if (list != null) {
			for (int i = 0, j = list.size(); i < j; i++) {
				col = 0;
				listValueVO = (ListValueVO) list.get(i);
				cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
				cell.setCellValue(listValueVO.getValue());
				cell = rowdata.createCell(BTSLUtil.parseIntToShort(col+1));
				cell.setCellValue(listValueVO.getLabel());
				row++;
				rowdata = worksheet2.createRow(row);
			}
		}
		return row;
	}  catch (Exception e) {
		_log.errorTrace(METHOD_NAME, e);
		_log.error(METHOD_NAME, " Exception e: " + e.getMessage());
	}
	return row;
}

private int writeUserPaymentTypeList(SXSSFSheet  worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) {
	final String METHOD_NAME = "writeUserPaymentTypeList";
	if (_log.isDebugEnabled()) {
		_log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
	}
	Row rowdata = null;
	Cell cell = null;
	String keyName = null;
	try {
		if(!BTSLUtil.isNullObject(p_messages))
		keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.paymenttypeheading");
		else
			keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.paymenttypeheading",null);
		rowdata = worksheet2.createRow(row);
		//cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell.setCellStyle(cellStyle);
		cell.setCellValue(keyName);
		worksheet2.addMergedRegion(new CellRangeAddress(row,  row, col,(col+2)));
		row++;
		col = 0;
		rowdata = worksheet2.createRow(row);
		if(!BTSLUtil.isNullObject(p_messages))
		keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.paymenttypeheading.note");
		else
			keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.paymenttypeheading.note",null);
		cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell.setCellValue(keyName);
		worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (COLUMN_MARGE)));
		row++;
		col = 0;

		rowdata = worksheet2.createRow(row);
		if(!BTSLUtil.isNullObject(p_messages))
		keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.paymenttypecode");
		else
			keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.paymenttypecode",null);
		cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell.setCellStyle(cellStyle);
		cell.setCellValue(keyName);
		col++;
		if(!BTSLUtil.isNullObject(p_messages))
		keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.paymenttypename");
		else
			keyName = RestAPIStringParser.getMessage(p_locale, "batchusercreation.mastersheet.paymenttypename",null);
		cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell.setCellStyle(cellStyle);
		cell.setCellValue(keyName);
		row++;
		rowdata = worksheet2.createRow(row);
		col = 0;

		ArrayList list = (ArrayList) p_hashMap.get(PretupsI.PAYMENT_INSTRUMENT_TYPE);
		ListValueVO listValueVO = null;
		if (list != null) {
			for (int i = 0, j = list.size(); i < j; i++) {
				col = 0;
				listValueVO = (ListValueVO) list.get(i);
				cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
				cell.setCellValue(listValueVO.getValue());
				cell = rowdata.createCell(BTSLUtil.parseIntToShort(col+1));
				cell.setCellValue(listValueVO.getLabel());
				row++;
				rowdata = worksheet2.createRow(row);
			}
		}
		return row;
	}  catch (Exception e) {
		_log.errorTrace(METHOD_NAME, e);
		_log.error(METHOD_NAME, " Exception e: " + e.getMessage());
	}
	return row;
}




	private int writeVoucherListing(SXSSFSheet  worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) {
		final String METHOD_NAME = "writeVoucherListing";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
		}
		Row rowdata = null;
		Cell cell = null;
		String keyName = null;
		try {
			if(!BTSLUtil.isNullObject(p_messages))
			keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.voucher");
			else
				keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.voucher",null);
			rowdata = worksheet2.createRow(row);
			//cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			worksheet2.addMergedRegion(new CellRangeAddress(row,  row, col,(col+2)));
			row++;
			col = 0;
			rowdata = worksheet2.createRow(row);
			if(!BTSLUtil.isNullObject(p_messages))
			keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.voucher.note");
			else
				keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchertype",null);
			//cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellValue(keyName);
			worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (COLUMN_MARGE)));
			row++;
			col = 0;
	
			rowdata = worksheet2.createRow(row);
			if(!BTSLUtil.isNullObject(p_messages))
			keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchertype");
			else
				keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchertype",null);
			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			col++;
			if(!BTSLUtil.isNullObject(p_messages))
			keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchername");
			else
				keyName = RestAPIStringParser.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchername",null);
			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
			cell.setCellStyle(cellStyle);
			cell.setCellValue(keyName);
			row++;
			rowdata = worksheet2.createRow(row);
			col = 0;
	
			ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_VOUCHERTYPE_LIST);
			ListValueVO listValueVO = null;
			if (list != null) {
				for (int i = 0, j = list.size(); i < j; i++) {
					col = 0;
					listValueVO = (ListValueVO) list.get(i);
					cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
					cell.setCellValue(listValueVO.getValue());
					cell = rowdata.createCell(BTSLUtil.parseIntToShort(col+1));
					cell.setCellValue(listValueVO.getLabel());
					row++;
					rowdata = worksheet2.createRow(row);
				}
			}
			return row;
		}  catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			_log.error(METHOD_NAME, " Exception e: " + e.getMessage());
		}
		return row;
	}
	
	public void writeBulkModifyExcel(String p_excelID, HashMap p_hashMap, Locale locale, String p_fileName, Connection p_con, String [] queryParams) {
    	final String METHOD_NAME = "writeModifyExcel";        
    	if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " p_excelID: " + p_excelID + " p_hashMap:" + p_hashMap + " p_locale: " + locale + " p_fileName: " + p_fileName);
        }
    	
    	Locale p_locale = null;
    	 
        boolean isFnameLnameAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED);
        boolean batchUserPasswordModifyAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PASSWD_MODIFY_ALLOWED);
        String pinPasswordEnDeCryptionType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE);
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        boolean rsaAuthenticationRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RSA_AUTHENTICATION_REQUIRED);
        boolean authTypeReq = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTH_TYPE_REQ);
        boolean batchUserProfileAssign = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN);
        boolean userVoucherTypeAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
        SXSSFSheet  worksheet1 = null, worksheet2 = null;
        int sheetCount = 0;
        int col = 0;
        int row = 0;
        int stepSize = 0;
        Row rowdata = null;
		Cell cell = null;
        String noOfRowsInOneTemplate = null; // No. of users data in one sheet
        FileOutputStream outputStream = null;
        SXSSFWorkbook  workbook = null;
        //Cell cell = null;
        CellStyle style = null;
        //Row rowdata = null;
        CreationHelper factory = null;
        try {
        	
        	workbook = new SXSSFWorkbook();  
        	workbook.setCompressTempFiles(true);
        	factory = workbook.getCreationHelper();
        	style = workbook.createCellStyle();
        	outputStream = new FileOutputStream(p_fileName);
        	Font times16font = workbook.createFont();
        	times16font.setFontName("Arial");
        	//times16font.setBoldweight((short) 8);
        	//times16font.setFontHeightInPoints((short) 14);
        	times16font.setFontHeightInPoints(BTSLUtil.parseIntToShort(14));
        	times16font.setBold(true);
        	style.setFont(times16font);
        	p_locale = locale;
            String keyName = null;
            String p_geographyCode = queryParams[0];
            String p_category_code= queryParams[1];
            String p_user_id = queryParams[2];
            String p_domain_name = queryParams[3];
            PreparedStatement pstmtSelect = null;
            ResultSet rs = null;
            ResultSet rsGeoDomain = null;
            String geoDomain = null;
            final StringBuilder strBuildGeodomain = new StringBuilder("SELECT grph_domain_code FROM user_geographies WHERE user_id=?");
            final String sqlSelectGeodomain = strBuildGeodomain.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "QUERY sqlSelectGeodomain=" + sqlSelectGeodomain);
            }

            ResultSet rsRoles = null;
            ResultSet rsRoles1 = null;
            String roles = null;
            final StringBuilder strBuildRoles = new StringBuilder("SELECT UR.role_code FROM user_roles UR, roles R ");
            strBuildRoles.append("WHERE UR.role_code=R.role_code AND R.status=? AND UR.user_id=? AND R.domain_type=? AND R.group_role=? ");
            final String sqlSelectRoles = strBuildRoles.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "QUERY sqlSelectRoles=" + sqlSelectRoles);
            }
           
            ResultSet rsServices = null;
            String services = null;
            final StringBuilder strBuildServices = new StringBuilder("SELECT US.service_type");
            strBuildServices.append(" FROM user_services US,users U,category_service_type CST");
            strBuildServices
                .append(" WHERE US.user_id=? AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type and CST.network_code=U.network_code");

            final String sqlSelectServices = strBuildServices.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "QUERY sqlSelectServices=" + sqlSelectServices);
            }
          //Voucher Type
            ResultSet rsVouchers = null;
            String vouchers = null;
            StringBuilder strBuildVouchers= new StringBuilder(" SELECT uv.voucher_type, vt.name ");
            strBuildVouchers.append(" FROM user_vouchertypes uv, voms_types vt, users u ");
            strBuildVouchers.append(" WHERE uv.user_id = ? AND uv.voucher_type = vt.voucher_type  AND u.user_id = uv.user_id ");
            final String sqlSelectVouchers = strBuildVouchers.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "QUERY sqlSelectVouchers=" + sqlSelectVouchers);
            }
      
            ResultSet rsSegments = null;
            String segments = null;
            StringBuffer strBuffSegments= new StringBuffer(" SELECT us.VOUCHER_SEGMENT, lu.LOOKUP_NAME ");
            strBuffSegments.append(" FROM USER_VOUCHER_SEGMENTS us, LOOKUPS lu, users u ");
            strBuffSegments.append(" WHERE us.user_id = ? AND us.VOUCHER_SEGMENT = lu.LOOKUP_CODE  AND u.user_id = us.user_id ");
            final String sqlSelectSegments = strBuffSegments.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "QUERY sqlSelectSegments=" + sqlSelectSegments);
            }
            
            ArrayList msisdnList = new ArrayList();
            ResultSet rsMsisdn = null;
            final StringBuilder strBuildMsisdn = new StringBuilder("SELECT user_phones_id,msisdn,primary_number,sms_pin,phone_language,country ");// phone_language,country
            strBuildMsisdn.append("FROM user_phones WHERE user_id=? ORDER BY primary_number DESC ");
            final String sqlSelectMsisdn = strBuildMsisdn.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "QUERY sqlSelectMsisdn=" + sqlSelectMsisdn);
            } 
            BatchUserCreationExcelRWPOIQry userCreationExcelRWPOIQry = (BatchUserCreationExcelRWPOIQry)
            		ObjectProducer.getObject(QueryConstants.BATCH_USER_CREATION_EXCEL_RW_POI_QRY, QueryConstants.QUERY_PRODUCER);
            try( 	PreparedStatement pstmtGeoDomain = p_con.prepareStatement(sqlSelectGeodomain);
            		PreparedStatement pstmtRoles = p_con.prepareStatement(sqlSelectRoles);
            		PreparedStatement pstmtServices = p_con.prepareStatement(sqlSelectServices);
            		PreparedStatement pstmtMsisdn = p_con.prepareStatement(sqlSelectMsisdn);
            		PreparedStatement pstmtVouchers = p_con.prepareStatement(sqlSelectVouchers);
            		PreparedStatement pstmtSegments = p_con.prepareStatement(sqlSelectSegments);) {
            	CategoryVO categoryVO = (CategoryVO) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_VO);
            	
               
               
                pstmtSelect = userCreationExcelRWPOIQry.writeModifyExcelQry(p_con, p_category_code, p_geographyCode, p_user_id);
                rs = pstmtSelect.executeQuery();
                int i = 1;
                ChannelUserVO channelUserVO = null;
                UserPhoneVO userPhoneVO = null;
                String password = null;
                noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHUSER");
                int noOfRowsPerTemplate = 0;
                if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
                    noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
                    noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
                } else {
                    noOfRowsPerTemplate = 1048500; // Default value of rows 
                }
                worksheet1 = (SXSSFSheet) workbook.createSheet("Template "+sheetCount);
                sheetCount++;
                worksheet1.setRandomAccessWindowSize(100);
                
                stepSize = noOfRowsPerTemplate;
    			rowdata = worksheet1.createRow(row);

    			String arr[] = {(String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME)};
    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_DOMAIN,arr);
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);			
    			row++;
                col = 0;
                keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_DOWNLOADED_BY, null);
                rowdata = worksheet1.createRow(row);
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			++col;
    			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellValue((String) p_hashMap.get(PretupsI.BATCH_USR_CREATED_BY));
                row++;
                col = 0;
                keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_DOMAIN_NAME, null);
                rowdata = worksheet1.createRow(row);
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			++col;
    			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellValue((String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME));
                row++;
                col = 0;
                keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_CATEGORY_NAME, null);
                rowdata = worksheet1.createRow(row);
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			++col;
    			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellValue((String) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_NAME));
                row++;
                col = 0;
                keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_GEOGRAPHY_NAME, null);
                rowdata = worksheet1.createRow(row);
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			col++;
    			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellValue((String) p_hashMap.get(PretupsI.BATCH_USR_GEOGRAPHY_NAME));
                row++;
                col = 0;

                keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_MANDATORY_MSG, null);
                rowdata = worksheet1.createRow(row);
                //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			worksheet1.addMergedRegion(new CellRangeAddress(row, row, col, (col+5)));
                row++;
                col = 0;
                keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_USER_ID, null);
                rowdata = worksheet1.createRow(row);
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_USER_ID_COMMENT, null));
    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_USER_NAME_PREFIX, null);
    			col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_USER_NAME_PREFIX_COMMENT, null));
                if (!isFnameLnameAllowed) {
                	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_USER_NAME, null);
                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
        			setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_USER_NAME_COMMENT, null));
                } else {
                	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_FIRST_NAME, null);
                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
        			setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_FIRST_NAME_COMMENT, null));

        			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_LAST_NAME, null);
                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
        			setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_LAST_NAME_COMMENT, null));
                }
                if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
                	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_WEB_LOGIN_ID, null);
                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
        			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_WEB_LOGIN_ID_COMMENT, null));
                    if (!"SHA".equalsIgnoreCase(pinPasswordEnDeCryptionType)) {
                        if (batchUserPasswordModifyAllow) {
                        	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_WEB_PASSWORD, null);
                            col++;
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                			cell.setCellStyle(style);
                			cell.setCellValue(keyName);
                        }
                    }
                }
                if (categoryVO.getSmsInterfaceAllowed().equals(PretupsI.YES)) {
                	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_MOBILE_NO, null);
                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
        			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_MOBILE_NO_COMMENT, null));
                    if (!"SHA".equalsIgnoreCase(pinPasswordEnDeCryptionType)) {
                    	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_PIN, null);
                        col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            			cell.setCellStyle(style);
            			cell.setCellValue(keyName);
                    }
                }
                keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_GEO_DOMAIN_CODE, null);
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_GEO_DOMAIN_CODE_COMMENT, null));
                if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
                	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_GRP_ROLE_CODE, null);
                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
        			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_GRP_ROLE_CODE_COMMENT, null));
        			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_ROLE_CODE, null);
                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
        			   setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_ROLE_CODE_COMMENT, null));
                }
                if (categoryVO.getServiceAllowed().equals(PretupsI.YES)) {
                	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_SERVICES, null);

                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
        			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_SERVICES_COMMENT, null));
            }

                keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_SHORT_NAME, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);

    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_SUBS_CODE, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);

    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_EXTERNAL_CODE, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_EXTERNAL_CODE_COMMENT, null));

    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_IN_SUSPEND, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			   setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_IN_SUSPEND_COMMENT, null));

    			   keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_OUT_SUSPEND, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			   setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_OUT_SUSPEND_COMMENT, null));

    			   keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_CONTACT_PERSON, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);

    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_CONTACT_NO, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);

    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_RSA_ID, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_RSA_ID_COMMENT, null));
    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_DESIGNATION, null);
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);

    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_ADDRESS1, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);

    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_ADDRESS2, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);

    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_CITY, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);

    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_STATE, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);

    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_COUNTRY, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_COMPANY, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);

    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_FAX, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);

    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_LANGUAGE, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_EMAIL, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);

                if (categoryVO.getOutletsAllowed().equals(PretupsI.YES)) {
                	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_OUTLET_CODE, null);

                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
        			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_SUB_OUTLET_CODE, null);

                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
                }
                if (PretupsI.SELECT_CHECKBOX.equals(categoryVO.getLowBalAlertAllow())) {
                	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_ALLOW_LOW_BAL_ALERT, null);

                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
                }

                if (isTrfRuleUserLevelAllow) {
                	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_TRF_ROLE_CODE, null);

                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
        			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_TRF_ROLE_CODE_COMMENT, null));

                }
                if (rsaAuthenticationRequired) {
                	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_RSA_AUTH, null);
                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
        			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_RSA_AUTH_COMMENT, null));
                }
                if (authTypeReq) {

                	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_AUTH_TYPE_ALLOWED, null);
                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
        			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_AUTH_TYPE_COMMENT, null));
                }
                keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_LONGITUDE, null);
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_LATITUDE, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_DOCUMENT_TYPE, null);
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_DOCUMENT_NO, null);
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
    			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_PAYMENT_TYPE, null);
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
                if (lmsAppl) {
                	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_LMS_PROFILE, null);
                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
                }
                if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
                	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_COMM_PROFILE, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellStyle(style);
                cell.setCellValue(keyName);
                setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_COMM_PROFILE_COMMENT, null));

                keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_TRF_PROFILE, null);

                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellStyle(style);
                cell.setCellValue(keyName);
                setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_TRF_PROFILE_COMMENT, null));
                keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_GRADE, null);
                col++;
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                cell.setCellStyle(style);
                cell.setCellValue(keyName);
                setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_GRADE_COMMENT, null));
                if(userVoucherTypeAllowed && p_hashMap.get(PretupsI.VOUCHER_TYPE_LIST)  != null){
                	keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_VOUCHER_TYPE, null);
                    col++;
                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
        			cell.setCellStyle(style);
        			cell.setCellValue(keyName);
        			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_VOUCHER_TYPE_COMMENT, null));
                   }
                }
                //heading of first sheet ends here
                col=0;
                channelUserVO = ChannelUserVO.getInstance();    
                userPhoneVO = new UserPhoneVO();
                msisdnList.clear();
                StringBuilder ashuBuild = new StringBuilder("");
                StringBuilder ashuMsisdn = new StringBuilder("");
                while (rs.next()) {
                    channelUserVO.setUserID(rs.getString("user_id"));
                    channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                    channelUserVO.setUserName(rs.getString("user_name"));
                    channelUserVO.setLoginID(rs.getString("login_id"));
                    channelUserVO.setCategoryName(rs.getString("category_name"));
                    password = rs.getString("password");
                    if (!BTSLUtil.isNullString(password)) {
                        channelUserVO.setPassword(BTSLUtil.decryptText(password));
                    } else {
                        channelUserVO.setPassword("");
                    }
                    userPhoneVO.setMsisdn(rs.getString("msisdn"));
                    userPhoneVO.setSmsPin(BTSLUtil.decryptText(rs.getString("sms_pin")));
                    channelUserVO.setUserPhoneVO(userPhoneVO);
                    channelUserVO.setDomainTypeCode(rs.getString("domain_type_code"));
                    i = 1;
                    pstmtGeoDomain.clearParameters();
                    pstmtGeoDomain.setString(i++, rs.getString("user_id"));
                    rsGeoDomain = pstmtGeoDomain.executeQuery();
                    while (rsGeoDomain.next()) {
                    	ashuBuild.append(", ").append(rsGeoDomain.getString("grph_domain_code"));
                    }
                    geoDomain = ashuBuild.toString();
                    if (!BTSLUtil.isNullString(geoDomain)) {
                        geoDomain = geoDomain.substring(1);
                    }
                    channelUserVO.setGeographicalCode(geoDomain);
                    ashuBuild.setLength(0); 
                    ashuBuild.trimToSize(); 
                    i = 1;
                    pstmtRoles.clearParameters();
                    pstmtRoles.setString(i++, TypesI.YES);
                    pstmtRoles.setString(i++, rs.getString("user_id"));
                    pstmtRoles.setString(i++, rs.getString("domain_type_code"));
                    pstmtRoles.setString(i++, PretupsI.YES);
                    rsRoles = pstmtRoles.executeQuery();
                    if (rsRoles.next()) {
                        channelUserVO.setGroupRoleCode(rsRoles.getString("role_code"));
                        channelUserVO.setGroupRoleFlag(PretupsI.YES);
                    } else {
                        i = 1;
                        pstmtRoles.clearParameters();
                        pstmtRoles.setString(i++, TypesI.YES);
                        pstmtRoles.setString(i++, rs.getString("user_id"));
                        pstmtRoles.setString(i++, rs.getString("domain_type_code"));
                        pstmtRoles.setString(i++, PretupsI.NO);
                        rsRoles1 = pstmtRoles.executeQuery();
                        while (rsRoles1.next()) {
                        	ashuBuild.append(", ").append(rsRoles1.getString("role_code"));
                        }
                        roles = ashuBuild.toString();
                        ashuBuild.setLength(0); 
                        ashuBuild.trimToSize(); 
                        if (!BTSLUtil.isNullString(roles)) {
                            roles = roles.substring(1);
                        }
                        channelUserVO.setGroupRoleCode(roles);
                        channelUserVO.setGroupRoleFlag(PretupsI.NO);
                    }

                    i = 1;
                    pstmtServices.clearParameters();
                    pstmtServices.setString(i++, rs.getString("user_id"));
                    rsServices = pstmtServices.executeQuery();                    
                    while (rsServices.next()) {
                    	ashuBuild.append(", ").append(rsServices.getString("service_type"));
                    }
                    services = ashuBuild.toString();
                    if (!BTSLUtil.isNullString(services)) {
                        services = services.substring(1);
                    }
                    //Voucher Type
                    ashuBuild.setLength(0); 
                    ashuBuild.trimToSize(); 
                    i = 1;
                    pstmtVouchers.clearParameters();
                    pstmtVouchers.setString(i++, rs.getString("user_id"));
                    rsVouchers = pstmtVouchers.executeQuery();                    
                    while (rsVouchers.next()) {
                    	ashuBuild.append(", ").append(rsVouchers.getString("voucher_type"));
                    }
                    vouchers = ashuBuild.toString();
                    if (!BTSLUtil.isNullString(vouchers)) {
                        vouchers = vouchers.substring(1);
                    }
                    
                    ashuBuild.setLength(0); 
                    ashuBuild.trimToSize(); 
                    i = 1;
                    pstmtSegments.clearParameters();
                    pstmtSegments.setString(i++, rs.getString("user_id"));
                    rsSegments = pstmtSegments.executeQuery();                    
                    while (rsSegments.next()) {
                    	ashuBuild.append(", ").append(rsSegments.getString("voucher_segment"));
                    }
                    segments = ashuBuild.toString();
                    if (!BTSLUtil.isNullString(segments)) {
                    	segments = segments.substring(1);
                    }
                                       
                    ashuBuild.setLength(0); 
                    ashuBuild.trimToSize(); 
                    channelUserVO.setServiceTypes(services);
                    channelUserVO.setVoucherTypes(vouchers);
                    channelUserVO.setSegments(segments);
                    channelUserVO.setShortName(rs.getString("short_name"));
                    channelUserVO.setEmpCode(rs.getString("employee_code"));
                    channelUserVO.setExternalCode(rs.getString("external_code"));
                    channelUserVO.setInSuspend(rs.getString("in_suspend"));
                    channelUserVO.setOutSuspened(rs.getString("out_suspend"));
                    channelUserVO.setContactPerson(rs.getString("contact_person"));
                    channelUserVO.setContactNo(rs.getString("contact_no"));
                    channelUserVO.setSsn(rs.getString("ssn"));
                    channelUserVO.setDesignation(rs.getString("designation"));
                    channelUserVO.setAddress1(rs.getString("address1"));
                    channelUserVO.setAddress2(rs.getString("address2"));
                    channelUserVO.setCity(rs.getString("city"));
                    channelUserVO.setState(rs.getString("state"));
                    channelUserVO.setCountry(rs.getString("country"));
                    channelUserVO.setEmail(rs.getString("email"));
                    channelUserVO.setCommissionProfileSetID(rs.getString("COMM_PROFILE_SET_ID"));
                    channelUserVO.setTransferProfileID(rs.getString("TRANSFER_PROFILE_ID"));
                    channelUserVO.setUserGrade(rs.getString("USER_GRADE"));
                    channelUserVO.setCompany(rs.getString("company"));
                    channelUserVO.setFax(rs.getString("fax"));
                    channelUserVO.setFirstName(rs.getString("firstname"));
                    channelUserVO.setLastName(rs.getString("lastname"));
                    channelUserVO.setOutletCode(rs.getString("outlet_code"));
                    channelUserVO.setSubOutletCode(rs.getString("suboutlet_code"));
                    channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                    channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
                    channelUserVO.setRsaFlag(rs.getString("rsaflag"));
                    channelUserVO.setAuthTypeAllowed(rs.getString("authentication_allowed"));
                    channelUserVO.setAlertEmail(rs.getString("alert_email"));
                    channelUserVO.setAlertType(rs.getString("alert_type"));
                    channelUserVO.setAlertMsisdn(rs.getString("alert_msisdn"));
                    pstmtMsisdn.clearParameters();
                    pstmtMsisdn.setString(1, rs.getString("user_id"));
                    rsMsisdn = pstmtMsisdn.executeQuery();
                    
                    String langcountry = null;
                    while (rsMsisdn.next()) {
                    	userPhoneVO = new UserPhoneVO();
                    	userPhoneVO.setMsisdn(rsMsisdn.getString("msisdn"));
                    	userPhoneVO.setSmsPin(BTSLUtil.decryptText(rsMsisdn.getString("sms_pin")));
                    	userPhoneVO.setUserPhonesId(rsMsisdn.getString("user_phones_id"));
                    	userPhoneVO.setCountry(rsMsisdn.getString("country"));// added
                    	langcountry = rsMsisdn.getString("phone_language") + "_" + rsMsisdn.getString("country");// added
                    	userPhoneVO.setPhoneLanguage(rsMsisdn.getString("phone_language")); // added
                    	msisdnList.add(userPhoneVO);
                    }
                    channelUserVO.setLanguage(langcountry); 
                    channelUserVO.setMsisdnList(msisdnList);
                    channelUserVO.setLongitude(rs.getString("longitude"));
                    channelUserVO.setLatitude(rs.getString("latitude"));
                    channelUserVO.setDocumentType(rs.getString("document_type"));
                    channelUserVO.setDocumentNo(rs.getString("document_no"));
                    channelUserVO.setPaymentType(rs.getString("payment_type"));
                    if (lmsAppl) {
                        channelUserVO.setLmsProfile(rs.getString("lms_profile"));
                    }
                    if((rs.getRow() % stepSize)==0) {
                    	 // Get the Java runtime
                        Runtime runtime = Runtime.getRuntime();
                        long memory = runtime.totalMemory() - runtime.freeMemory();
                        _log.debug("writeModifyExcel","Used memory is megabytes: " + (memory)/1048576);
                        // Run the garbage collector
                        runtime.gc();
                        // Calculate the used memory
                        memory = runtime.totalMemory() - runtime.freeMemory();
                        _log.debug("writeModifyExcel","Used memory is megabytes: " + (memory)/1048576);
                    	worksheet1 = (SXSSFSheet) workbook.createSheet("Template "+sheetCount);
                    	sheetCount++;
                    	worksheet1.setRandomAccessWindowSize(100);
                    	row =0; col = 0;           			
            			rowdata = worksheet1.createRow(row);       			
            			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_DOMAIN,arr);
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);			
              			row++;
                          col = 0;
                          keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_DOWNLOADED_BY, null);
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			++col;
              			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellValue((String) p_hashMap.get(PretupsI.BATCH_USR_CREATED_BY));
                          row++;
                          col = 0;
                          keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_DOMAIN_NAME, null);
                          rowdata = worksheet1.createRow(row);
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			++col;
              			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellValue((String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME));
                          row++;
                          col = 0;
                          keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_CATEGORY_NAME, null);
                          rowdata = worksheet1.createRow(row);
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			++col;
              			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellValue((String) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_NAME));
                          row++;
                          col = 0;
                          keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_GEOGRAPHY_NAME, null);
                          rowdata = worksheet1.createRow(row);
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			col++;
              			cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellValue((String) p_hashMap.get(PretupsI.BATCH_USR_GEOGRAPHY_NAME));
                          row++;
                          col = 0;

                          keyName =  RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_MANDATORY_MSG, null);
                          rowdata = worksheet1.createRow(row);
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			worksheet1.addMergedRegion(new CellRangeAddress(row, row, col, (col+5)));
                          row++;
                          col = 0;
                          keyName =  RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_USER_ID, null);
                          rowdata = worksheet1.createRow(row);
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_USER_ID_COMMENT, null));
              		
              			keyName =  RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_USER_NAME_PREFIX, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_USER_NAME_PREFIX_COMMENT, null));

                          if (!isFnameLnameAllowed) {
                        	  keyName =   RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_USER_NAME, null);
                              col++;
                              cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                  			cell.setCellStyle(style);
                  			cell.setCellValue(keyName);
                  			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_USER_NAME_COMMENT, null));
                          } else {
                        	  keyName =   RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_FIRST_NAME, null);
                              col++;
                              cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                  			cell.setCellStyle(style);
                  			cell.setCellValue(keyName);
                  			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_FIRST_NAME_COMMENT, null));

                  			keyName = 	RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_LAST_NAME, null);
                              col++;
                              cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                  			cell.setCellStyle(style);
                  			cell.setCellValue(keyName);
                  			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_LAST_NAME_COMMENT, null));
                          }
                          if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
                        	  keyName =  	RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_WEB_LOGIN_ID, null);
                              col++;
                              cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                  			cell.setCellStyle(style);
                  			cell.setCellValue(keyName);
                  			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_WEB_LOGIN_ID_COMMENT, null));
                              if (!"SHA".equalsIgnoreCase(pinPasswordEnDeCryptionType)) {
                                  if (batchUserPasswordModifyAllow) {
                                	  keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_WEB_PASSWORD, null);
                                      col++;
                                      cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                          			cell.setCellStyle(style);
                          			cell.setCellValue(keyName);
                                  }
                              }
                          }
                          if (categoryVO.getSmsInterfaceAllowed().equals(PretupsI.YES)) {
                        	  keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_MOBILE_NO, null);
                              col++;
                              cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                  			cell.setCellStyle(style);
                  			cell.setCellValue(keyName);
                  			setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_MOBILE_NO_COMMENT, null));
                              if (!"SHA".equalsIgnoreCase(pinPasswordEnDeCryptionType)) {
                            	  keyName = 	RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_PIN, null);
                                  col++;
                                  cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                      			cell.setCellStyle(style);
                      			cell.setCellValue(keyName);
                              }
                          }
                          keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_GEO_DOMAIN_CODE, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			setCellComment(cell,RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_GEO_DOMAIN_CODE_COMMENT, null));
                          if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
                        	  keyName = 	RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_GRP_ROLE_CODE, null);
                              col++;
                              cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                  			cell.setCellStyle(style);
                  			cell.setCellValue(keyName);
                  			setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_GRP_ROLE_CODE_COMMENT, null));
                  			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_ROLE_CODE, null);
                              col++;
                              cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                  			cell.setCellStyle(style);
                  			cell.setCellValue(keyName);
                  			setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_ROLE_CODE_COMMENT, null));
                          }
                          if (categoryVO.getServiceAllowed().equals(PretupsI.YES)) {
                        	  keyName =  	RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_SERVICES, null);
                              col++;
                              cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                  			cell.setCellStyle(style);
                  			cell.setCellValue(keyName);
                  			setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_SERVICES_COMMENT, null));
                          }

                          keyName =  RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_SHORT_NAME, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);

              			keyName =   RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_SUBS_CODE, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);

              			keyName =  RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_EXTERNAL_CODE, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_EXTERNAL_CODE_COMMENT, null));

              			keyName =  RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_IN_SUSPEND, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_IN_SUSPEND_COMMENT, null));

              			keyName =  RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_OUT_SUSPEND, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_OUT_SUSPEND_COMMENT, null));

              			keyName =  RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_CONTACT_PERSON, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);

              			keyName =  RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_CONTACT_NO, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);

              			keyName =   RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_RSA_ID, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_RSA_ID_COMMENT, null));
              			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_DESIGNATION, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);

              			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_ADDRESS1, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);

              			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_ADDRESS2, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);

              			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_CITY, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);

              			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_STATE, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);

              			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_COUNTRY, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_COMPANY, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);

              			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_FAX, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);

              			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_LANGUAGE, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_EMAIL, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);

                          if (categoryVO.getOutletsAllowed().equals(PretupsI.YES)) {
                        	  keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_OUTLET_CODE, null);
                              col++;
                              cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                  			cell.setCellStyle(style);
                  			cell.setCellValue(keyName);
                  			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_SUB_OUTLET_CODE, null);
                              col++;
                              cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                  			cell.setCellStyle(style);
                  			cell.setCellValue(keyName);
                          }
                          if (PretupsI.SELECT_CHECKBOX.equals(categoryVO.getLowBalAlertAllow())) {
                        	  keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_ALLOW_LOW_BAL_ALERT, null);
                              col++;
                              cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                  			cell.setCellStyle(style);
                  			cell.setCellValue(keyName);
                          }

                          if (isTrfRuleUserLevelAllow) {
                        	  keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_TRF_ROLE_CODE, null);
                              col++;
                              cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                  			cell.setCellStyle(style);
                  			cell.setCellValue(keyName);
                  			setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_TRF_ROLE_CODE_COMMENT, null));
                          }
                          if (rsaAuthenticationRequired) {
                        	  keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_RSA_AUTH, null);
                              col++;
                              cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                  			cell.setCellStyle(style);
                  			cell.setCellValue(keyName);
                  			setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_RSA_AUTH_COMMENT, null));
                          }
                          if (authTypeReq) {

                        	  keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_AUTH_TYPE_ALLOWED, null);
                              col++;
                              cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                  			cell.setCellStyle(style);
                  			cell.setCellValue(keyName);
                  			setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_AUTH_TYPE_COMMENT, null));
                          }
                          keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_LONGITUDE, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_LATITUDE, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
              			cell.setCellStyle(style);
              			cell.setCellValue(keyName);
              			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_DOCUMENT_TYPE, null);
                        col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            			cell.setCellStyle(style);
            			cell.setCellValue(keyName);
            			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_DOCUMENT_NO, null);
                        col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            			cell.setCellStyle(style);
            			cell.setCellValue(keyName);
            			keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_PAYMENT_TYPE, null);
                        col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            			cell.setCellStyle(style);
            			cell.setCellValue(keyName);
                          if (lmsAppl) {
                        	  keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_LMS_PROFILE, null);
                              col++;
                              cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                  			cell.setCellStyle(style);
                  			cell.setCellValue(keyName);
                          }
                          if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
                        	  keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_COMM_PROFILE, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                          cell.setCellStyle(style);
                          cell.setCellValue(keyName);
                          setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_COMM_PROFILE_COMMENT, null));
                          keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_TRF_PROFILE, null);                        
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                          cell.setCellStyle(style);
                          cell.setCellValue(keyName);
                          setCellComment(cell, RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_TRF_PROFILE_COMMENT, null));
                          keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_GRADE, null);
                          col++;
                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                          cell.setCellStyle(style);
                          cell.setCellValue(keyName);
                          setCellComment(cell,p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.grade.comment"));
                          if(userVoucherTypeAllowed && p_hashMap.get(PretupsI.VOUCHER_TYPE_LIST)  != null){
                        	  keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_VOUCHER_TYPE, null);
                  			col++;
	                          cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
	              			  cell.setCellStyle(style);
	              			  cell.setCellValue(keyName);
	                         }
                          }  

                    	stepSize += noOfRowsPerTemplate;
                    }        			
                    String msisdn = null;
                    String pin = null;
                        row++;
                        col = 0;
                        rowdata = worksheet1.createRow(row);
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getUserID());
            			 col++;
                         cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
             			 cell.setCellValue(channelUserVO.getUserNamePrefix());
                        if (!isFnameLnameAllowed) {
                        	col++;
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                			
                			cell.setCellValue(channelUserVO.getUserName());
                        } else {
                        	col++;
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));        			
                			cell.setCellValue(channelUserVO.getFirstName());        			
                        	col++;
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                			cell.setCellValue(channelUserVO.getLastName());
                        }
                        if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
                        	col++;
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));                			
                			cell.setCellValue(channelUserVO.getLoginID());
                            if (!"SHA".equalsIgnoreCase(pinPasswordEnDeCryptionType)) {
                                if (batchUserPasswordModifyAllow) {
                                	col++;
                                    cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                        			cell.setCellValue(BatchUserUpdateAction.XLS_PINPASSWARD);
                                }
                            }
                        }
                        if (categoryVO.getSmsInterfaceAllowed().equals(PretupsI.YES)) {
                            msisdnList = channelUserVO.getMsisdnList();
                            msisdn = "";
                            pin = "";
                            for (int k = 0, length = msisdnList.size(); k < length; k++) {
                                userPhoneVO = (UserPhoneVO) msisdnList.get(k);
                                ashuMsisdn.append(userPhoneVO.getMsisdn()).append(",");
                                ashuBuild.append(BatchUserUpdateAction.XLS_PINPASSWARD).append(",");
                            }
                            msisdn = ashuMsisdn.toString();
                            pin  = ashuBuild.toString();
                            if (msisdn.length() > 1) {
                                msisdn = msisdn.substring(0, msisdn.length() - 1);
                            }
                            if (pin.length() > 1) {
                                pin = pin.substring(0, pin.length() - 1);
                            }

                            col++;
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));                			
                			cell.setCellValue(msisdn);
                            if (!"SHA".equalsIgnoreCase(pinPasswordEnDeCryptionType)) {
                            	 col++;
                                 cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));                    			
                     			cell.setCellValue(pin);
                            }
                            ashuMsisdn.setLength(0); 
                            ashuMsisdn.trimToSize(); 
                            ashuBuild.setLength(0); 
                            ashuBuild.trimToSize(); 
                        }
                        col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getGeographicalCode());
                        if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
                        	 col++;
                             cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                 			 cell.setCellValue(channelUserVO.getGroupRoleFlag());
                 			 col++;
                             cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));                			
                 			cell.setCellValue(channelUserVO.getGroupRoleCode());
                        }

                        if (categoryVO.getServiceAllowed().equals(PretupsI.YES)) {
                        	 col++;
                             cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                 			
                 			cell.setCellValue(channelUserVO.getServiceTypes());
                        }
                        col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getShortName());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getEmpCode());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getExternalCode());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getInSuspend());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));           			
            			cell.setCellValue(channelUserVO.getOutSuspened());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getContactPerson());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getContactNo());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));           			
            			cell.setCellValue(channelUserVO.getSsn());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            			cell.setCellValue(channelUserVO.getDesignation());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));           			
            			cell.setCellValue(channelUserVO.getAddress1());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getAddress2());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            			cell.setCellValue(channelUserVO.getCity());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getState());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getCountry());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getCompany());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
            			cell.setCellValue(channelUserVO.getFax());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getLanguage());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getEmail());
                        if (categoryVO.getOutletsAllowed().equals(PretupsI.YES)) {
                        	col++;
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));               			
                			cell.setCellValue(channelUserVO.getOutletCode());
                			col++;
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));                			
                			cell.setCellValue(channelUserVO.getSubOutletCode());
                        }
                        if (PretupsI.SELECT_CHECKBOX.equals(categoryVO.getLowBalAlertAllow())) {
                        	col++;
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                			
                			cell.setCellValue(channelUserVO.getLowBalAlertAllow());
                        }
                        if (isTrfRuleUserLevelAllow) {
                            col++;
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                			
                			cell.setCellValue(channelUserVO.getTrannferRuleTypeId());
                        }
                        if (rsaAuthenticationRequired) {
                        	col++;
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                			
                			cell.setCellValue(channelUserVO.getRsaFlag());
                        }
                        if (authTypeReq) {
                        	col++;
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                			
                			cell.setCellValue(channelUserVO.getAuthTypeAllowed());
                        }
                    	col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));           			
            			cell.setCellValue(channelUserVO.getLongitude());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getLatitude());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getDocumentType());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getDocumentNo());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getPaymentType());
                        if (lmsAppl) {
                        	col++;
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
                		    cell.setCellValue(channelUserVO.getLmsProfile());
                        }   
                        col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getCommissionProfileSetID());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getTransferProfileID());
            			col++;
                        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
            			cell.setCellValue(channelUserVO.getUserGrade());
                       
            			if(userVoucherTypeAllowed && p_hashMap.get(PretupsI.VOUCHER_TYPE_LIST)  != null){
            				col++;
                            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));            			
                			cell.setCellValue(channelUserVO.getVoucherTypes());
            			}
            			channelUserVO.clearInstance();
                        userPhoneVO.clearInstance();
                        msisdnList.clear();
                }   //result set iteration ends here*/

                //now write master data sheet
                int masterRow = 0; int masterCol = 0;
                worksheet2 = (SXSSFSheet)workbook.createSheet("MasterSheet");
                worksheet2.setRandomAccessWindowSize(1000);
                String str[]= {p_domain_name};
          	    keyName = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_UMOD_MASTER_DATA_DOMAIN, str);
                rowdata = worksheet2.createRow(masterRow);
                cell = rowdata.createCell(BTSLUtil.parseIntToShort(masterCol));
    			cell.setCellStyle(style);
    			cell.setCellValue(keyName);
                worksheet2.addMergedRegion(new CellRangeAddress(masterRow, masterRow, masterCol, (masterCol + 1)));
                
                masterRow++;
                masterRow++;
                masterCol = 0;
                masterRow = this.writeUserPrefix(worksheet2, masterCol, masterRow, p_hashMap,style);
                masterRow++;
                masterCol = 0;
                masterRow = this.writeGeographyListingForUpdate(worksheet2, masterCol, masterRow, p_hashMap,style);
                masterRow++;
                masterCol = 0;
                masterRow = this.writeLanguageList(worksheet2, masterCol, masterRow, p_hashMap,style);
                if (categoryVO.getOutletsAllowed().equals(PretupsI.YES)) {
                	masterRow++;
                	masterCol = 0;
                    masterRow = this.writeUserOutletSubOutlet(worksheet2, masterCol, masterRow, p_hashMap,style);
                }
                if (categoryVO.getServiceAllowed().equals(PretupsI.YES)) {
                	masterRow++;
                	masterCol = 0;
                    masterRow = this.writeServiceType(worksheet2, masterCol, masterRow, p_hashMap,style);
                }
                if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
                	masterRow++;
                	masterCol = 0;
                    masterRow = this.writeRoleCode(worksheet2, masterCol, masterRow, p_hashMap,style);
                    masterRow++;
                    masterCol = 0;
                    masterRow = this.writeGroupRoleCode(worksheet2, masterCol, masterRow, p_hashMap,style);
                }
                if (rsaAuthenticationRequired) {
                	masterRow++;
                	masterCol = 0;
                    masterRow = this.writeRsaAssociations(worksheet2, masterCol, masterRow, p_hashMap,style);
                }
                if (authTypeReq) {
                	masterRow++;
                	masterCol = 0;
                }
                if (isTrfRuleUserLevelAllow) {
                	masterRow++;
                	masterCol = 0;
                    masterRow = this.writeTrfRuleType(worksheet2, masterCol, masterRow, p_hashMap,style);
                }
                if (lmsAppl) {
                	masterRow++;
                	masterCol = 0;
                    masterRow = this.writeLmsProfile(worksheet2, masterCol, masterRow, p_hashMap,style);
                }
                masterRow++;
            	masterCol = 0;
                masterRow = this.writeCommissionData(worksheet2, masterCol, masterRow, p_hashMap,style);
                masterRow++;
                masterCol = 0;
                masterRow = this.writeCategoryData(worksheet2, masterCol, masterRow, p_hashMap,style);
                
                masterRow++;
            	masterCol = 0;
                masterRow = this.writeGradeData(worksheet2, masterCol, masterRow, p_hashMap,style);
                masterRow++;
                masterCol = 0;
                masterRow = this.writeUserDocumentTypeList(worksheet2, masterCol, masterRow, p_hashMap,style);
                masterRow++;
                masterCol = 0;
                masterRow = this.writeUserPaymentTypeList(worksheet2, masterCol, masterRow, p_hashMap,style);
                if(userVoucherTypeAllowed && p_hashMap.get(PretupsI.VOUCHER_TYPE_LIST)  != null){
                	ArrayList list = (ArrayList) p_hashMap.get(PretupsI.VOUCHER_TYPE_LIST);
                	if (list != null && list.size() > 0) {
		                masterRow++;
		                masterCol = 0;
		                masterRow = this.writeVoucherListing(worksheet2, masterCol, masterRow, p_hashMap,style); 
                	}
                } 
                
                workbook.write(outputStream);
                workbook.dispose();
                channelUserVO = null;
                userPhoneVO=null;
                msisdnList=null;
                Runtime runtime = Runtime.getRuntime();
                long memory = runtime.totalMemory() - runtime.freeMemory();
                _log.debug("writeBulkModifyExcel","Used memory is megabytes: " + (memory)/1048576);
                // Run the garbage collector
                runtime.gc();
                // Calculate the used memory
                memory = runtime.totalMemory() - runtime.freeMemory();
                _log.debug("writeBulkModifyExcel","Used memory is megabytes: " + (memory)/1048576);
            } catch (SQLException sqe) {
                _log.error(METHOD_NAME, "SQLException : " + sqe);
                _log.errorTrace(METHOD_NAME, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchUserListForModify]", "", "", "",
                    "GeographyCode=" + p_geographyCode + "SQL Exception:" + sqe.getMessage());
                throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
            } catch (Exception ex) {
                _log.error(METHOD_NAME, "Exception : " + ex);
                _log.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchUserListForModify]", "", "", "",
                    "GeographyCode=" + p_geographyCode + "SQL Exception:" + ex.getMessage());
                throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
            } finally {
            	try {
                    if (rsRoles != null) {
                       rsRoles.close();
                   }
               } catch (Exception e) {
                   _log.errorTrace(METHOD_NAME, e);
               }
              try {
                  if (rsRoles1 != null) {
                     rsRoles1.close();
                 }
             } catch (Exception e) {
                 _log.errorTrace(METHOD_NAME, e);
             }
            	try {
                    if (outputStream != null) {
                    	outputStream.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            	try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (pstmtSelect != null) {
                        pstmtSelect.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (rsGeoDomain != null) {
                        rsGeoDomain.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
               
               
              
                try {
                    if (rsServices != null) {
                        rsServices.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (rsVouchers != null) {
                    	rsVouchers.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (rsSegments != null) {
                    	rsSegments.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (rsMsisdn != null) {
                        rsMsisdn.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }           
            }         
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeBulkModifyExcel", " Exception e: " + e.getMessage());
        } finally {
            
            worksheet1 = null;
            worksheet2 = null;
            workbook = null;
            if (_log.isDebugEnabled()) {
                _log.debug("writeBulkModifyExcel", " Exiting");
            }
        }
    }
	
	private int writeUserLoanProfile(SXSSFSheet  worksheet2, int col, int row, HashMap p_hashMap, CellStyle cellStyle) throws Exception {
	    
		 final String METHOD_NAME = "writeUserLoanProfile";
			   if (_log.isDebugEnabled()) {
		            _log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
		        }
		       
		          Row rowdata = null;
		          Cell cell = null;
		        try {
		            String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.loanprofileheading");
		            rowdata = worksheet2.createRow(row);
		            cell = rowdata.createCell((short) col);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(keyName);
					worksheet2.addMergedRegion(new CellRangeAddress(row,  row, col,(col+2)));
		            row++;
		            col = 0;
		            rowdata = worksheet2.createRow(row);
		            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.loanprofileheading.note");
		            cell = rowdata.createCell((short) col);
					cell.setCellValue(keyName);
					worksheet2.addMergedRegion(new CellRangeAddress(row, row, col, (COLUMN_MARGE)));
		            row++;
		            col = 0;
		            
		            rowdata = worksheet2.createRow(row);
		            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.loanprofilecode");
		            cell = rowdata.createCell((short) col);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(keyName);
					col++;
		            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.loanprofilename");
		            cell = rowdata.createCell((short) col);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(keyName);
					col++;
		            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.loanprofilecategoryid");
		            cell = rowdata.createCell((short) col);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(keyName);
					col++;
		            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.loanprofilecategoryname");
		            cell = rowdata.createCell((short) col);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(keyName);
					
		            row++;
		            rowdata = worksheet2.createRow(row);
		            col = 0;

		            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_LOAN_PROFILE_LIST);
		            ListValueVO listValueVO = null;
		            if (list != null) {
		                for (int i = 0, j = list.size(); i < j; i++) {
		                    col = 0;
		                    listValueVO = (ListValueVO) list.get(i);
		                    cell = rowdata.createCell((short) col++);
		                    if (_log.isDebugEnabled())
		                    _log.debug(METHOD_NAME,"The Value:-" +listValueVO.getValue()+"The Value of i:-"+i+"The Lable"+listValueVO.getLabel());
		                    cell.setCellValue(listValueVO.getValue());	 
		                    
		                    cell = rowdata.createCell((short) col++);
		                    
		                    cell.setCellValue(listValueVO.getLabel().split("_")[0]);
		        			cell = rowdata.createCell((short) (col++));
		        			
		        			cell.setCellValue(listValueVO.getLabel().split("_")[1]);
		        			cell = rowdata.createCell((short) (col++));
		        			
		        			cell.setCellValue(listValueVO.getLabel().split("_")[2]);
		        			cell = rowdata.createCell((short) (col++));
		        			
		        			        			      			
		                    row++;
		                    rowdata = worksheet2.createRow(row);
		                }
		            }
		            return row;
		        }  catch (Exception e) {
		            _log.errorTrace(METHOD_NAME, e);
		            _log.error(METHOD_NAME, " Exception e: " + e.getMessage());
		            throw e;
		        }
		    }

	
	
	
}