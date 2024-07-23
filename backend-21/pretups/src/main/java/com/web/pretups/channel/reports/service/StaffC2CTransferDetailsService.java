package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author pankaj.kumar
 *
 */
public interface StaffC2CTransferDetailsService {


	/**
	 * @param request
	 * @param response
	 * @param usersReportModel
	 * @param userVO
	 * @param model
	 */
	public 	void loadStaffC2CTransferDetails(HttpServletRequest request,HttpServletResponse response, UsersReportModel usersReportModel,UserVO userVO, Model model) ;

	

	/**
	 * @param request
	 * @param response
	 * @param usersReportModel
	 * @param userVO
	 * @param model
	 * @param bindingResult
	 * @return
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws ValidatorException 
	 */
	public 	boolean displayStaffC2CTransferDetailsList(HttpServletRequest request,HttpServletResponse response, UsersReportModel usersReportModel,UserVO userVO, Model model,BindingResult bindingResult) throws ValidatorException, IOException, SAXException;


	/**
	 * @param usersReportModel
	 * @return
	 * @throws InterruptedException 
	 * @throws BTSLBaseException 
	 * @throws SQLException 
	 */
	public	String downloadCSVReportStaffC2CTransferDetailsFile(UsersReportModel usersReportModel) throws InterruptedException, BTSLBaseException, SQLException ;

	

	
	/**
	 * @param userVO
	 * @param zoneCode
	 * @param domainCode
	 * @param userName
	 * @param parentCategoryCode
	 * @param usersReportModel
	 * @return
	 */
	public List<ChannelUserTransferVO> loadC2cUserLists(UserVO userVO,String zoneCode,String domainCode,String userName,String parentCategoryCode,UsersReportModel usersReportModel);
	
	
	/**
	 * @param userVO
	 * @param zoneCode
	 * @param domainCode
	 * @param userName
	 * @param parentCategoryCode
	 * @param usersReportModel
	 * @param parentUserID
	 * @return
	 */
	public List<ChannelUserTransferVO> loadUseridforO2c(UserVO userVO,String zoneCode,String domainCode,String userName,String parentCategoryCode,UsersReportModel usersReportModel,String parentUserID);
	
	


	
	}  


