package com.web.pretups.channel.reports.service;


import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.UsersReportModel;

public interface O2CTransferDetailsService {

	public void loadO2CTransferDetails(HttpServletRequest request,HttpServletResponse response,UsersReportModel usersReportModel,UserVO userVO,Model model) throws IOException,BTSLBaseException,ParseException,ServletException;
	
	public boolean loadEnquiryList(HttpServletRequest request,HttpServletResponse response,UsersReportModel usersReportModel,UserVO userVO,Model model,BindingResult bindingResult) throws ValidatorException, IOException, SAXException;
	
	public List<ListValueVO> loadC2cFromUserList(UserVO userVO,String zoneCode,String domainCode,String UserName,String fromtransferCategoryCode,UsersReportModel usersReportModel);
	
	//public String downloadCSVReportFile(UsersReportModel usersReportModel) throws BTSLBaseException;
	
	public String downloadFileforEnq(UsersReportModel usersReportModel)throws BTSLBaseException, SQLException, InterruptedException;
}
