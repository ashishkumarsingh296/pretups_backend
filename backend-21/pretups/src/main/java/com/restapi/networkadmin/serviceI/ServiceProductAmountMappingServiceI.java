package com.restapi.networkadmin.serviceI;

import java.sql.Connection;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.requestVO.AddServiceProductAmountMappingRequestVO;
import com.restapi.networkadmin.responseVO.AddServiceProductAmountDetailsResponseVO;
import com.restapi.networkadmin.responseVO.LoadServiceAndProductListResponseVO;
import com.restapi.networkadmin.responseVO.SelectorServiceProductAmountDetailsResponseVO;
@Service
public interface ServiceProductAmountMappingServiceI {
	public LoadServiceAndProductListResponseVO  loadServiceAndProductList(Connection con, UserVO userVO)throws BTSLBaseException, Exception;
	public SelectorServiceProductAmountDetailsResponseVO loadSelectorAmountDetails(Connection con, UserVO userVO) throws BTSLBaseException,Exception;
	public AddServiceProductAmountDetailsResponseVO addServiceProductAmountMappingDetails(Connection con, UserVO userVO
			,AddServiceProductAmountMappingRequestVO requestVO) throws BTSLBaseException,Exception;
	public int modifyServiceProductAmountMapping(Connection con,UserVO userVO, AddServiceProductAmountMappingRequestVO request) throws BTSLBaseException,Exception;
	public int deleteServiceProductAmountMapping(Connection con,UserVO userVO,String serviceId,String productId)throws BTSLBaseException,Exception;
	
}
