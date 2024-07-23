package com.btsl.pretups.interfaces.service;

import java.io.IOException;
import java.util.List;

import org.springframework.validation.BindingResult;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.user.businesslogic.UserVO;

public interface InterfaceManagementService {

	public List<ListValueVO> loadInterfaceCategory() throws IOException, RuntimeException;
	public Boolean loadInterfaceDetails(InterfaceVO interfaceCategoryCode, UserVO userVO, BindingResult bindingResult, List<InterfaceVO> list) throws IOException, RuntimeException;
	public Boolean deleteInterface(String interfaceID, String interfaceTypeID, BindingResult bindingResult, UserVO userVO) throws IOException, RuntimeException;
}
