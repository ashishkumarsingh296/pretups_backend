package com.btsl.pretups.master.service;


import java.io.InputStream;

import org.springframework.ui.ModelMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.UserVO;

/**
 *   Interface which provides base for BatchGeographicalDomainServiceImpl class
 * also declares different method for creation of batch geographical domain creation functionality
 * 
 * @author vikas.chaudhary
 * @since 21/10/2016
 */
public interface BatchGeographicalDomainService {

	public InputStream downloadList(ModelMap modelMap) throws BTSLBaseException;

	public void initiateBatchGeographicalDomainCreation(String batchName, String filePath, String fileName,UserVO userVO, ModelMap modelMap) throws BTSLBaseException;
	
}
