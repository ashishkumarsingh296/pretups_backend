package com.btsl.pretups.network.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.user.businesslogic.UserVO;



@Service
public interface ChangeNetworkService {

	
	 public List<NetworkVO> loadData(String loginId, Model model)throws BTSLBaseException;
	 public void processData(UserVO userVO,String networkCode, Model model)throws BTSLBaseException;
}
