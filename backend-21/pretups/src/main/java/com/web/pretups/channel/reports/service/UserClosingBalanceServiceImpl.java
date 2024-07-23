package com.web.pretups.channel.reports.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO;
import com.btsl.pretups.channel.reports.businesslogic.UserClosingBalanceVO;
import com.btsl.pretups.channel.reports.businesslogic.UserReportDAO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

@Service("userClosingBalanceService")
public class UserClosingBalanceServiceImpl implements UserClosingBalanceService {


	public static final Log _log = LogFactory.getLog(UserClosingBalanceServiceImpl.class.getName());
	private static final String SUCCESS_KEY = "success";
	private static final String FAIL_KEY = "fail";



	
	@Override
	public UsersReportModel loadUserClosingBalance(UserVO userVO) {
		
        final String methodName = "loadUserClosingBalance";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		UsersReportModel userForm = new UsersReportModel();
		 try {
			 mcomCon = new MComConnection();
			 con = mcomCon.getConnection();
			 
			 userForm.setUserType(userVO.getUserType());
			 userForm.setLoginUserID(userVO.getUserID());
	            if(TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode()))
	            	userForm.setZoneList(new GeographicalDomainDAO().loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID()));
			    else
			    	userForm.setZoneList(userVO.getGeographicalAreaList());
	            userForm.setDomainList(BTSLUtil.displayDomainList(userVO.getDomainList()));
	            userForm.setNetworkCode(userVO.getNetworkID());
	            userForm.setNetworkName(userVO.getNetworkName());
	            ArrayList loggedInUserDomainList = new ArrayList();
	            commonUserList(userForm, loggedInUserDomainList, userVO);
	            final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
	            
	            if ((PretupsI.OPERATOR_USER_TYPE).equals(userVO.getUserType())) {
	            	userForm.setParentCategoryList(categoryWebDAO.loadCategoryReportList(con));
	            } else if ((PretupsI.CHANNEL_USER_TYPE).equals(userVO.getUserType())) {
	                final int loginSeqNo = userVO.getCategoryVO().getSequenceNumber();
	                userForm.setParentCategoryList(categoryWebDAO.loadCategoryReporSeqtList(con, loginSeqNo));
	            }
	            final ListValueVO listValueVO = null;

	            commonGeographicDetails(userForm, listValueVO);

			 
	            
	            
			 
		} catch (Exception e) {
			_log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);;
		}finally{
			if(mcomCon != null){mcomCon.close("UserClosingBalanceServiceImpl#loadUserClosingBalance");mcomCon=null;}
		}
		
		
		return userForm;
	}
	
	
    protected void commonUserList(UsersReportModel thisForm, ArrayList loggedInUserDomainList, UserVO userVO) {
        if(loggedInUserDomainList==null) {
            loggedInUserDomainList= new ArrayList();
        }
           if (thisForm.getDomainListSize() == 0) {
            loggedInUserDomainList.add(new ListValueVO(userVO.getDomainName(), userVO.getDomainID()));
            thisForm.setDomainList(loggedInUserDomainList);
            thisForm.setDomainCode(userVO.getDomainID());
            thisForm.setDomainName(userVO.getDomainName());
        } else if (thisForm.getDomainListSize() == 1) {
            ListValueVO listvo = (ListValueVO) thisForm.getDomainList().get(0);
            thisForm.setDomainCode(listvo.getValue());
            thisForm.setDomainName(listvo.getLabel());
        }
    }
	
	
    protected void commonGeographicDetails(UsersReportModel thisForm, ListValueVO listValueVO) {

        ArrayList zoneList = thisForm.getZoneList();
        UserGeographiesVO geographyVO = null;
        ArrayList geoList = new ArrayList();

        for (int i = 0, k = zoneList.size(); i < k; i++) {
            geographyVO = (UserGeographiesVO) zoneList.get(i);
            geoList.add(new ListValueVO(geographyVO.getGraphDomainName(), geographyVO.getGraphDomainCode()));
        }
        if (geoList.size() == 1) {
            listValueVO = (ListValueVO) geoList.get(0);
            thisForm.setZoneCode(listValueVO.getValue());
            thisForm.setZoneName(listValueVO.getLabel());
            thisForm.setZoneList(geoList);
        } else {
            thisForm.setZoneList(geoList);
        }
    }


	@Override
	public ArrayList loadUserList(UserVO userVO, String parentCategoryCode,
			String domainList, String zoneList, String userName) {
		
        final String methodName = "loadUserList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, PretupsI.ENTERED);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserReportDAO channelUserDAO = null;
        ArrayList userList = new ArrayList();
        try {

            
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            channelUserDAO = new ChannelUserReportDAO();

            String[] arr = parentCategoryCode.split("\\|");

            if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                if (parentCategoryCode.equals(PretupsI.ALL)) {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategoryHierarchy(con, PretupsI.ALL, domainList, zoneList, userName, userVO.getUserID());
                } else {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategoryHierarchy(con, arr[1], domainList, zoneList, userName, userVO.getUserID());
                }
            } else {
                if (parentCategoryCode.equals(PretupsI.ALL)) {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategory(con, PretupsI.ALL, domainList, zoneList, userName, userVO.getUserID());
                } else {
                    userList = channelUserDAO.loadUserListBasisOfZoneDomainCategory(con, arr[1], domainList, zoneList, userName, userVO.getUserID());
                }
            }


        } catch (Exception e) {
            _log.error(methodName, "Exceptin:e=" + e);
            _log.errorTrace(methodName, e);
        } finally {
        	if(mcomCon != null){
        		mcomCon.close("UserClosingBalanceServiceImpl#loadUserList");
        	mcomCon=null;
        	}

        }
		
		
		return userList;
	}


	@Override
	public boolean downloadClosingBalance(Model model, UserVO userVO, UsersReportModel userForm, UsersReportModel sessionUserReportForm, HttpServletRequest request, HttpServletResponse response,  BindingResult bindingResult) throws ValidatorException, IOException, SAXException {
		
        final String methodName = "downloadClosingBalance";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, PretupsI.ENTERED);
        }

        
        String tempCatCode = null;
        ListValueVO listValueVO = null;
        ArrayList userList;
        ArrayList userVOList;
        Connection con = null;MComConnectionI mcomCon = null;
     
        boolean intermediateError = false;
        String parentCategoryCode = null;
        CommonValidator commonValidator=new CommonValidator("configfiles/reports/validation-userClosingBalance.xml", userForm, "UsersReportForm");
		Map<String, String> errorMessages = commonValidator.validateModel();
		PretupsRestUtil pru=new PretupsRestUtil();
		pru.processFieldError(errorMessages, bindingResult);
		if(!(sessionUserReportForm.getZoneList().isEmpty())){
    		
    		userForm.setZoneList(sessionUserReportForm.getZoneList());
    	}
		if(!(sessionUserReportForm.getDomainList().isEmpty())){
    		
    		userForm.setDomainList(sessionUserReportForm.getDomainList());
    	}
		if(!(sessionUserReportForm.getParentCategoryList().isEmpty())){
    		
    		userForm.setParentCategoryList(sessionUserReportForm.getParentCategoryList());
    	}
		if(bindingResult.hasFieldErrors()){
			
			 request.getSession().setAttribute("SessionUserForm", userForm);
			return false;
		} 
    	
        request.getSession().setAttribute("SessionUserForm", userForm);
        model.addAttribute("UsersReportForm", sessionUserReportForm);
		
    	final UsersReportModel thisForm = (UsersReportModel)userForm;
    	request.getSession().setAttribute("SessionUserForm", thisForm);
      try{
    	  

      	mcomCon = new MComConnection();con=mcomCon.getConnection();

      	
          tempCatCode = thisForm.getParentCategoryCode();
          if (thisForm.getZoneCode().equals(TypesI.ALL)) {
              thisForm.setZoneName(PretupsRestUtil.getMessageString("list.all"));
          } else {
              listValueVO = BTSLUtil.getOptionDesc(thisForm.getZoneCode(), thisForm.getZoneList());
              thisForm.setZoneName(listValueVO.getLabel());
          }
          listValueVO = BTSLUtil.getOptionDesc(thisForm.getDomainCode(), thisForm.getDomainList());
          thisForm.setDomainName(listValueVO.getLabel());
          final ChannelUserReportDAO channelUserReportDAO = new ChannelUserReportDAO();
          parentCategoryCode = thisForm.getParentCategoryCode();
			String userName = thisForm .getUserName();
			thisForm.setUserName(userName);
			if (!BTSLUtil.isNullString(userName)) {
				String[] parts = userName.split("\\(");
				userName = parts[0];
				thisForm.setUserName(userName);
				
				if(parts.length > 1){
					String userID = parts[1];
					userID = userID.replaceAll("\\)", "");
					thisForm.setUserID(userID);
					}
				 
          if (thisForm.getParentCategoryCode().equals(TypesI.ALL)) {
              thisForm.setParentCategoryCode(TypesI.ALL);
              thisForm.setCategoryName(PretupsRestUtil.getMessageString("list.all"));
              userList = channelUserReportDAO.loadUserListBasisOfZoneDomainCategory(con, PretupsI.ALL, thisForm.getDomainCode(), thisForm.getZoneCode(), thisForm
                  .getUserName(), userVO.getUserID());
          } else {
              listValueVO = BTSLUtil.getOptionDesc(thisForm.getParentCategoryCode(), thisForm.getParentCategoryList());
              thisForm.setCategoryName(listValueVO.getLabel());

              final String[] arr = thisForm.getParentCategoryCode().split("\\|");
              thisForm.setParentCategoryCode(arr[1]);
              userList = channelUserReportDAO.loadUserListBasisOfZoneDomainCategory(con, arr[1], thisForm.getDomainCode(), thisForm.getZoneCode(), thisForm.getUserName(),
                  userVO.getUserID());
          }
          if (thisForm.getUserName().equalsIgnoreCase(PretupsRestUtil.getMessageString("list.all"))) {
              thisForm.setUserID(PretupsI.ALL);
          } else if (userList == null || userList.isEmpty()) {
              intermediateError = true;
              thisForm.setParentCategoryCode(tempCatCode);
				model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("user.selectcategoryforedit.error.usernotexist"));
				return false;

          } else if (userList.size() == 1) {
              final ChannelUserTransferVO channelUserTransferVO = (ChannelUserTransferVO) userList.get(0);
              thisForm.setUserName(channelUserTransferVO.getUserName());
              thisForm.setUserID(channelUserTransferVO.getUserID());
          } else if (userList.size() > 1) {

              boolean flag = true;
              if (!BTSLUtil.isNullString(thisForm.getUserID())) {
                  for (int i = 0, j = userList.size(); i < j; i++) {
                      final ChannelUserTransferVO channelUserTransferVO = (ChannelUserTransferVO) userList.get(i);
                      if (thisForm.getUserID().equals(channelUserTransferVO.getUserID()) && thisForm.getUserName().equalsIgnoreCase(channelUserTransferVO.getUserName())) {
                          thisForm.setUserName(channelUserTransferVO.getUserName());
                          flag = false;
                          break;
                      }
                  }
              }
              if (flag) {
            	  model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("user.selectcategoryforedit.error.usermorethanone"));
                  thisForm.setParentCategoryCode(tempCatCode);
                  return false;
              }
          }
          java.sql.Date fromDate = null;
          java.sql.Date toDate = null;
          fromDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(thisForm.getFromDate()));
          String strToDate = null;
          String fromAmount = "";
          String toAmount = "";
          if (BTSLUtil.isNullString(thisForm.getToDate())) {
              toDate = BTSLUtil.getSQLDateFromUtilDate(new Date());
              strToDate = BTSLUtil.getDateStringFromDate(new Date());
              thisForm.setToDate(strToDate);
          } else {
              toDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(thisForm.getToDate()));
          }
          if (BTSLUtil.isNullString(userForm.getFromAmount())) {
              fromAmount = "0";
          } else {
              fromAmount = userForm.getFromAmount();
          }
          if (BTSLUtil.isNullString(userForm.getToAmount())) {
              toAmount = "999999999";
          } else {
              toAmount = userForm.getToAmount();
          }
          if (!intermediateError) {
              userVOList = new UserReportDAO().loadUserClosingBalance(con, userVO.getNetworkID(), thisForm.getZoneCode(), thisForm.getDomainCode(), thisForm
                  .getParentCategoryCode(), thisForm.getUserID(), userVO.getUserID(), fromDate, toDate, fromAmount, toAmount, userVO.getUserType());
              if (userVOList != null && !userVOList.isEmpty()) {
               loadDownloadFile(thisForm, request, response, userVO, userVOList, parentCategoryCode);
              } else {
                
                  model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("user.closing.balance.no.data.found"));
                  return false;
              }

          }

      
    	  
      }else{
    	  model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("user.selectcategoryforedit.error.usernotexist"));
    	  return false;
      } 
      }
    	 catch (Exception e) {
      
          _log.error(methodName, "Exceptin:e=" + e);
          _log.errorTrace(methodName, e);
      }finally {
      	if(mcomCon != null){mcomCon.close("UserClosingBalanceServiceImpl#downloadClosingBalance");mcomCon=null;}
      }
      request.getSession().setAttribute("SessionUserForm", thisForm);
      model.addAttribute("UsersReportForm", thisForm);
    	return true;

		
		
	}
	
	public void loadDownloadFile(UsersReportModel form, HttpServletRequest request, HttpServletResponse response, UserVO userVO, ArrayList p_userVOList, String parentCategoryCode) throws BTSLBaseException {
        final String methodName = "loadDownloadFile";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
       
        final HashMap excelDataMap = new HashMap();
        final UsersReportModel thisForm = (UsersReportModel) form;
		InputStream is = null;
		OutputStream os = null;
        try {
            excelDataMap.put("EXCEL_WRITE_DATA", p_userVOList);
            excelDataMap.put("EXCEL_ZONE_NAME", thisForm.getZoneName());
            excelDataMap.put("EXCEL_DOMAIN_NAME", thisForm.getDomainName());
            excelDataMap.put("EXCEL_CATEGORY_NAME", thisForm.getCategoryName());
            excelDataMap.put("EXCEL_USER_NAME", thisForm.getUserName());
            excelDataMap.put("EXCEL_FROM_DATE", thisForm.getFromDate());
            excelDataMap.put("EXCEL_TO_DATE", thisForm.getToDate());
            excelDataMap.put("EXCEL_FROM_AMOUNT", thisForm.getFromAmount());
            excelDataMap.put("EXCEL_TO_AMOUNT", thisForm.getToAmount());

            final int noOfDays = BTSLUtil.getDifferenceInUtilDates(BTSLUtil.getDateFromDateString(thisForm.getFromDate()), BTSLUtil
                .getDateFromDateString(thisForm.getToDate()));
            if (noOfDays >= 0) {
                excelDataMap.put("EXCEL_NO_DAYS", new Integer(noOfDays));
            }
            String filePath = Constants.getProperty("DOWNLOADUSERCLOSINGBALANCEPATH");
            
                final File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }
           
                thisForm.setParentCategoryCode(parentCategoryCode);

                //throw new BTSLBaseException(this, methodName, "bulkuser.bulkusermodify.downloadfile.error.dirnotcreated", "loadUserClosingBalPage");
                
         
            final String fileName = Constants.getProperty("DOWNLOADUSERCLOSINGBALANCENAME") + BTSLUtil.getFileNameStringFromDate(new Date()) + ".xls";

            // Call the ExcelWrite Method.. & write in XLS file for Master Data
            // Creation.
            
            this.writeUserBalanceXls(ExcelFileIDI.USER_CLOSING_BAL, excelDataMap, BTSLUtil.getBTSLLocale(request), filePath + fileName);
            
            String fileLocation = filePath + fileName;

            File file = new File(fileLocation);
	        is = new FileInputStream(file);
	        response.setContentType("application/vnd.ms-excel");
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
	        os = response.getOutputStream();
	        
	        byte[] buffer = new byte[1024];
	        int len;
	        while ((len = is.read(buffer)) != -1) {
	            os.write(buffer, 0, len);
	        }
	        os.flush();
            // for security
            // filePath=BTSLUtil.encryptText(filePath);
            filePath = BTSLUtil.encrypt3DesAesText(filePath);

        } catch (Exception e) {
            _log.error(methodName, "Exception:e=" + e);
            _log.errorTrace(methodName, e);
           
        } finally {
			try{
        		if(is!=null){
        			is.close();	
        		}
        	}catch(Exception e){
        		 _log.errorTrace(methodName, e);
        	}
        	try{
        		if(os!=null){
        			os.close();	
        		}
        	}catch(Exception e){
        		 _log.errorTrace(methodName, e);
        	}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting=" );
            }
        }
        
    }
	
	   public void writeUserBalanceXls(String p_excelID, HashMap p_hashMap, Locale locale, String p_fileName) throws IOException, WriteException, ParseException, BTSLBaseException {
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
	   
	   
	   
	   private void writeModifyInDataSheet(WritableSheet worksheet1, int col, int row, HashMap p_hashMap) throws  WriteException, ParseException {
	        final String METHOD_NAME = "writeModifyInDataSheet";
	        if (_log.isDebugEnabled()) {
	            _log.debug("writeModifyInDataSheet", " p_hashMap size=" + p_hashMap.size() + " col=" + col + " row=" + row);
	        }

	        ArrayList balanceVOList = null;
	        UserClosingBalanceVO cloBalVO = null;
	        try {
	            col = 1;
	            WritableFont times16font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
	            WritableCellFormat times16format = new WritableCellFormat(times16font);
	            // times16format.setBackground(Colour.BROWN);
	            // times16format.setWrap(true);
	            // times16format.setAlignment(Alignment.JUSTIFY);

	            WritableFont times12font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
	            WritableCellFormat times12format = new WritableCellFormat(times12font);
	           
	            String keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.heading");

				Label label = new Label(col, row, keyName, times12format);
	            worksheet1.mergeCells(col, row, col + 5, row);
	            worksheet1.addCell(label);
	            row = row + 2;
	            col = 0;

	           
	           keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.header.geographyname");
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.addCell(label);

	            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_ZONE_NAME"));
	            worksheet1.addCell(label);
	            col = col + 5;

	           
	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.channelcategoryuserbalance.label.channeldomain");
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.addCell(label);

	            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_DOMAIN_NAME"));
	            worksheet1.addCell(label);
	            row++;
	            col = 0;

	      
	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.channelcategoryuserbalance.label.channelcategory");
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.addCell(label);

	            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_CATEGORY_NAME"));
	            worksheet1.addCell(label);
	            col = col + 5;

	           
	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.channelcategoryuserbalance.label.channelcategoryuser");
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.addCell(label);

	            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_USER_NAME"));
	            worksheet1.addCell(label);
	            row++;
	            col = 0;

	            
	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.selectstocktxn.label.fromdate");
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.addCell(label);

	            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_FROM_DATE"));
	            worksheet1.addCell(label);
	            col = col + 5;

	            
	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.selectstocktxn.label.todate");
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.addCell(label);

	            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_TO_DATE"));
	            worksheet1.addCell(label);
	            row++;
	            col = 0;

	          
	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.label.fromamount");
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.addCell(label);

	            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_FROM_AMOUNT"));
	            worksheet1.addCell(label);
	            col = col + 5;


	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.label.toamount");
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.addCell(label);

	            label = new Label(++col, row, (String) p_hashMap.get("EXCEL_TO_AMOUNT"));
	            worksheet1.addCell(label);
	            row = row + 2;
	            col = 0;

	           
	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.channelcategoryuserbalance.label.channelcategoryuser");
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);

	            
	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.msisdn");
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);

	 
	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.channelcategoryuserbalance.label.channelcategory");
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);

	
	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.header.geographyname");
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);


	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.parentName");
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);

	
	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.parentMsisdn");
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);

	  
	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.ownerName");
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);


	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.ownermobileNo");
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);
	            
	            keyName = PretupsRestUtil.getMessageString("pretups.userClosingBalance.productCode");
	            label = new Label(col++, row, keyName, times16format);
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

	            int colBeforeDate = 8;
	            int daysFromStartDate;
	            String[] dateBalArr;
	            String balanceDateStr = "";
	            long balance = 0;
	            Number number = null;
	            String productCodeOld = null;
	            String productCodeNew = null;
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
	                    		 if((colOld!=0)&&(productCodeOld!=null)&&( colOld == colnew )&&!(productCodeOld.equals(productCodeNew))){
	         	                   
	                    			 rowNew=row+1;
	                    			 label = new Label(col, rowNew, productCodeNew );
	        	                    worksheet1.addCell(label);
	 	 	                        balance = new Long(balDt.split("::")[2]);
	 	 	                        
		 	                        number = new Number(colnew, rowNew, Double.valueOf(PretupsBL.getDisplayAmount(balance)));
		 	                        worksheet1.addCell(number);
	                    			 
	                    		 }else{
	         	                    label = new Label(col, row, productCodeNew);
	        	                    worksheet1.addCell(label);
	 	 	                        balance = new Long(balDt.split("::")[2]);
		 	                        number = new Number(colnew, row, Double.valueOf(PretupsBL.getDisplayAmount(balance)));
		 	                        worksheet1.addCell(number);
	                    		 }

	 	                       productCodeOld = productCodeNew;
	 	                       colOld = colnew;
	                    	
	                       
	                    }
	                    if((rowNew!=0)&&(rowNew > row)){
	                    	row = rowNew;
	                    }
	                    productCodeOld = null;
	                    productCodeNew = null;
	                } else {
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
