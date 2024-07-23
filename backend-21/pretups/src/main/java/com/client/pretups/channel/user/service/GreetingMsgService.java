package com.client.pretups.channel.user.service;

import java.io.InputStream;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;

/*
 *  * Interface which provides base for GreetingMsgServiceImpl class
 *   * also declares different method for Greeting Msg functionalities
 *    */
public interface GreetingMsgService {


	public List<ListValueVO> loadCategory() throws BTSLBaseException, Exception ;
	public List<ListValueVO> loadDomain() throws BTSLBaseException, Exception;
	public InputStream downloadUserList(String p_domain , String p_category , String p_geography,String p_loginID) throws BTSLBaseException, Exception;
	
	public <T> void processListValueVOValue(List<T> listObject, String type) throws Exception;
}


