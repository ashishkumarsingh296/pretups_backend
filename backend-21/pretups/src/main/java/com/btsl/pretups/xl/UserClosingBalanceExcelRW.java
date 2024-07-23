package com.btsl.pretups.xl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.UserClosingBalanceVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class UserClosingBalanceExcelRW {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private WritableFont times12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
    private WritableCellFormat times12format = new WritableCellFormat(times12font);
    private WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
    private WritableCellFormat times16format = new WritableCellFormat(times16font);
    private MessageResources p_messages = null;
    private Locale p_locale = null;


    /*
     * @method name : writeUserBalanceXls.
     * 
     * @author : Rajdeep.
     * This method will write xls file.
     * p_excelID: String
     * p_hashMap: HashMap
     * messages : MessageResources
     * locale : Locale
     * p_fileName: String
     * return : void
     */
    public void writeUserBalanceXls(String p_excelID, HashMap p_hashMap, MessageResources messages, Locale locale, String p_fileName) throws BTSLBaseException {
        final String METHOD_NAME = "writeUserBalanceXls";
        if (_log.isDebugEnabled()) {
            _log.debug("writeUserBalanceXls", " p_excelID: " + p_excelID + " p_hashMap:" + p_hashMap + " p_locale: " + locale + " p_fileName: " + p_fileName);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        int col = 0;
        int row = 0;
        String noOfRowsInOneTemplate = null; // No. of users data in one sheet
        int noOfTotalSheet = 0;
        int userListSize = 0;
        ArrayList userClosingVOList = null;
        try {
            p_messages = messages;
            p_locale = locale;

            workbook = Workbook.createWorkbook(new File(p_fileName));
            userClosingVOList = (ArrayList) p_hashMap.get("EXCEL_WRITE_DATA");
            userListSize = userClosingVOList.size();
            noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHUSER");
            int noOfRowsPerTemplate = 0;
            if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
                noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
                noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
            } else {
                noOfRowsPerTemplate = 65500; // Default value of rows
            }

            // Number of sheet to display the user list
            noOfTotalSheet = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(userListSize) / noOfRowsPerTemplate));

            if (noOfTotalSheet > 1) {
                int i = 0;
                int k = 0;
                ArrayList tempList = new ArrayList();
                for (i = 0; i < noOfTotalSheet; i++) {
                    tempList.clear();
                    if (k + noOfRowsPerTemplate < userListSize) {
                        for (int j = k; j < k + noOfRowsPerTemplate; j++) {
                            tempList.add(userClosingVOList.get(j));
                        }
                    } else {
                        for (int j = k; j < userListSize; j++) {
                            tempList.add(userClosingVOList.get(j));
                        }
                    }
                    p_hashMap.put("EXCEL_WRITE_DATA", tempList);
                    worksheet1 = workbook.createSheet("Data Sheet " + (i + 1), i);
                    this.writeModifyInDataSheet(worksheet1, col, row, p_hashMap);
                    k = k + noOfRowsPerTemplate;
                }
            } else {
                worksheet1 = workbook.createSheet("Data Sheet", 0);
                this.writeModifyInDataSheet(worksheet1, col, row, p_hashMap);
            }
            workbook.write();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserBalanceXls", " Exception e: " + e.getMessage());
            throw new BTSLBaseException(e);
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            worksheet1 = null;
            workbook = null;
            if (_log.isDebugEnabled()) {
                _log.debug("writeUserBalanceXls", " Exiting");
            }
        }

    }

    private void writeModifyInDataSheet(WritableSheet worksheet1, int col, int row, HashMap p_hashMap) throws Exception {
        final String METHOD_NAME = "writeModifyInDataSheet";
        if (_log.isDebugEnabled()) {
            _log.debug("writeModifyInDataSheet", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }

        ArrayList balanceVOList = null;
        UserClosingBalanceVO cloBalVO = null;
        try {
            col = 1;
            String keyName = p_messages.getMessage(p_locale, "user.closing.balance.xlsfile.heading");

            Label label = new Label(col, row, keyName, times12format);
            worksheet1.mergeCells(col, row, col + 5, row);
            worksheet1.addCell(label);
            row = row + 2;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "bulkuser.modify.xlsfile.header.geographyname");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_ZONE_NAME"));
            worksheet1.addCell(label);
            col = col + 5;

            keyName = p_messages.getMessage(p_locale, "bulkuser.modify.xlsfile.header.domainname");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_DOMAIN_NAME"));
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "bulkuser.modify.xlsfile.header.categoryname");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_CATEGORY_NAME"));
            worksheet1.addCell(label);
            col = col + 5;

            keyName = p_messages.getMessage(p_locale, "user.closing.balance.user.name");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_USER_NAME"));
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "label.formdate");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_FROM_DATE"));
            worksheet1.addCell(label);
            col = col + 5;

            keyName = p_messages.getMessage(p_locale, "label.todate");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_TO_DATE"));
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "user.report.closing.balance.label.fromamount");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_FROM_AMOUNT"));
            worksheet1.addCell(label);
            col = col + 5;

            keyName = p_messages.getMessage(p_locale, "user.report.closing.balance.label.toamount");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_TO_AMOUNT"));
            worksheet1.addCell(label);
            row = row + 2;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "user.closing.balance.user.name");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "user.closing.balance.user.msisdn");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "user.closing.balance.user.category");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "user.closing.balance.user.geography");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "user.closing.balance.user.parent.name");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "user.closing.balance.user.parent.msisdn");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "pretups.userClosingBalance.ownerName");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "pretups.userClosingBalance.ownermobileNo");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
			
			keyName = p_messages.getMessage(p_locale,"user.closing.balance.user.grand.name");
        	label = new Label(col++,row,keyName, times16format);
        	worksheet1.addCell(label);

        	keyName = p_messages.getMessage(p_locale,"user.closing.balance.user.grand.msisdn");
        	label = new Label(col++,row,keyName, times16format);
        	worksheet1.addCell(label);

            int noOfDays = ((Integer) p_hashMap.get("EXCEL_NO_DAYS")).intValue() + 1;
            Date fromDate = BTSLUtil.getDateFromDateString((String) p_hashMap.get("EXCEL_FROM_DATE"));
            Date toDate = BTSLUtil.getDateFromDateString((String) p_hashMap.get("EXCEL_TO_DATE"));
            if (fromDate.compareTo(toDate) != 0) {
                Date tempDate = fromDate;
                for (int i = 0; i < noOfDays; i++) {

                    if (!tempDate.after(toDate)) {
                        label = new Label(col++, row, BTSLUtil.getDateStringFromDate(tempDate), times16format);
                        worksheet1.addCell(label);
                    }
                    tempDate = BTSLUtil.addDaysInUtilDate(tempDate, 1);
                }
            } else {
                label = new Label(col++, row, BTSLUtil.getDateStringFromDate(fromDate), times16format);
                worksheet1.addCell(label);
            }
            balanceVOList = (ArrayList) p_hashMap.get("EXCEL_WRITE_DATA");
            row++;
            col = 0;

            int colBeforeDate = 10;
            int daysFromStartDate;
            String[] dateBalArr;
            String balanceDateStr = "";
            long balance = 0;
            Number number = null;
            String productCodeOld = null;
            String productCodeNew = null;
			String balancestr=null;
            int colOld = 0;
            int rowNew = 0;
            for (int i = 0, j = balanceVOList.size(); i < j; i++) {
                row++;
                col = 0;
                cloBalVO = (UserClosingBalanceVO) balanceVOList.get(i);
                balanceDateStr = cloBalVO.getBalanceString();
                // if(cloBalVO.getBalanceString()!=null){
                if (!BTSLUtil.isNullString(balanceDateStr)) {
                    label = new Label(col++, row, cloBalVO.getUserName());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, cloBalVO.getUserMSISDN());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, cloBalVO.getUserCategory());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, cloBalVO.getUserGeography());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, cloBalVO.getParentUserName());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, cloBalVO.getParentUserMSISDN());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, cloBalVO.getOwnerUserName());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, cloBalVO.getOwnerUserMSISDN());
                    worksheet1.addCell(label);
					
					label = new Label(col++,row,cloBalVO.getGrandUserName());
        			worksheet1.addCell(label);

        			label = new Label(col++,row,cloBalVO.getGrandUserMSISDN());
        			worksheet1.addCell(label);
                    // Process the balance string
                    dateBalArr = balanceDateStr.split(",");
                    for (String balDt : dateBalArr) {
                    	if(QueryConstants.DB_POSTGRESQL.equals(Constants.getProperty(QueryConstants.PRETUPS_DB))){
                    		String balDt1 = balDt.split("::")[1];
                    		daysFromStartDate = BTSLUtil.getDifferenceInUtilDates(fromDate, BTSLUtil.getDateFromDateString(balDt1.split("\\s+")[0], "yyyy-MM-dd"));
                    	}else{
                    		daysFromStartDate = BTSLUtil.getDifferenceInUtilDates(fromDate, BTSLUtil.getDateFromDateString(balDt.split("::")[1], "dd-MMM-yy"));
                    	}
                    	int colnew = colBeforeDate + daysFromStartDate+1;
                    		  productCodeNew = balDt.split("::")[0];
                    		 if((colOld!=0)&&(productCodeOld!=null)&&( colOld == colnew )&& !(productCodeOld.equals(productCodeNew))){
         	                   
                    			 rowNew=row+1;
                    		//	 label = new Label(col, rowNew, productCodeNew );
        	                    //worksheet1.addCell(label);
 	 	                        balance = new Long(balDt.split("::")[2]);
 	 	                        //number = new Number(colnew, rowNew, Double.valueOf(PretupsBL.getDisplayAmount(balance)));
								//worksheet1.addCell(number);
								balancestr=productCodeNew+":"+Double.valueOf(PretupsBL.getDisplayAmount(balance));
								label = new Label(col-1, rowNew, balancestr);
								worksheet1.addCell(label);
	 	                        
                    			 
                    		 }else{
         	                    label = new Label(col, row, productCodeNew);
        	                    //worksheet1.addCell(label);
 	 	                        balance = new Long(balDt.split("::")[2]);
	 	                        //number = new Number(colnew, row, Double.valueOf(PretupsBL.getDisplayAmount(balance)));
								//worksheet1.addCell(number);
								balancestr=productCodeNew+":"+Double.valueOf(PretupsBL.getDisplayAmount(balance));
								label = new Label(col++, row, balancestr);
								worksheet1.addCell(label);
	 	                        
                    		 }

 	                       productCodeOld = productCodeNew;
 	                       colOld = colnew;
                    	
                       
                    }
                    if((rowNew!=0)&&(rowNew > row)){
                    	row = rowNew;
                    }
                    productCodeOld = null;
                    productCodeNew = null;
                }  else {
                    label = new Label(col++, row, cloBalVO.getUserName());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, cloBalVO.getUserMSISDN());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, cloBalVO.getUserCategory());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, cloBalVO.getUserGeography());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, cloBalVO.getParentUserName());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, cloBalVO.getParentUserMSISDN());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, cloBalVO.getOwnerUserName());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, cloBalVO.getOwnerUserMSISDN());
                    worksheet1.addCell(label);
					
					label = new Label(col++,row,cloBalVO.getGrandUserName());
        			worksheet1.addCell(label);
        			
        			label = new Label(col++,row,cloBalVO.getGrandUserMSISDN());
        			worksheet1.addCell(label);
					
					
                }
            }

        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheet", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheet", " Exception e: " + e.getMessage());
            throw e;
        } finally {
            p_hashMap = null;
        }
    }

}
