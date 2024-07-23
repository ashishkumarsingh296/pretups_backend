package com.btsl.pretups.subscriber.service;

import java.io.IOException;
import java.util.List;

import org.springframework.validation.BindingResult;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.master.businesslogic.SubLookUpVO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.btsl.user.businesslogic.UserVO;
/*
 * Interface which provides base for BarredUserServiceImpl class
 * also declares different method for Bar User functionalities
 */
public interface BarredUserService {

	public List<ListValueVO> loadModules(String userType) throws  IOException;
	public List<ListValueVO> loadUserType(String userType) throws  IOException;
	public List<SubLookUpVO> loadBarringType(String module, String userType) throws  IOException ;
	public Boolean addBarUser(BarredUserVO barUser, UserVO userVO, BindingResult bindingResult) throws  IOException;
	public Boolean viewBarUserList(BarredUserVO barUser, UserVO userVO, BindingResult bindingResult, List<BarredUserVO> barredUserList) throws IOException, RuntimeException;
	public Boolean processUnBarUser(BarredUserVO barUser, UserVO userVO, BindingResult bindingResult, List<BarredUserVO> barredUserList) throws IOException, RuntimeException;
	public Boolean processSelectedBarredUserToUnbar(BarredUserVO barUser, UserVO userVO, BindingResult bindingResult) throws IOException, RuntimeException;
}
