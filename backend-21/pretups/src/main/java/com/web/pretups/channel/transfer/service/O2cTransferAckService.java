package com.web.pretups.channel.transfer.service;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.channel.transfer.web.ChannelTransferAckModel;



/**
 * @author pankaj.kumar
 *
 */
public interface O2cTransferAckService {

	
	/**
	 * @param request
	 * @param response
	 * @param channelTransferAskModel
	 * @param userVO
	 * @param model
	 * @param bindingResult
	 * @return
	 * @throws ValidatorException
	 * @throws IOException
	 * @throws SAXException
	 */
	public boolean loadTransferAckList(HttpServletRequest request,HttpServletResponse response,ChannelTransferAckModel channelTransferAskModel,UserVO userVO,Model model,BindingResult bindingResult) throws ValidatorException, IOException, SAXException;
		
	/**
	 * @param channelTransferAckModel
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public String downloadFileforAck(ChannelTransferAckModel channelTransferAckModel)throws BTSLBaseException, SQLException, InterruptedException;

 
	
	
}
