package com.restapi.superadmin.serviceI;

import java.sql.Connection;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.restapi.superadmin.DomainListResponseVO;
import com.restapi.superadmin.requestVO.ChannelTransferRuleRequestVO;
import com.restapi.superadmin.responseVO.ChannelTransferRuleViewResponseVO;
import com.restapi.superadminVO.ChannelTransferRuleVO;

@Service
public interface ChannelToChannelTransferRuleManagementServiceI {
	
	public DomainListResponseVO viewDomainList(Connection con) throws BTSLBaseException;

	public ArrayList loadChannelTransferRuleVOList(Connection con, String networkCode,
			String domainCode, String toDomainCode, String Type) throws BTSLBaseException ;
	public Integer updateChannelTransferRule(Connection con, ChannelTransferRuleVO channelTransferRuleVO)throws BTSLBaseException,  Exception;

	public Integer deleteChannelTransferRule(Connection con, ChannelTransferRuleVO channelTransferRuleVO)throws BTSLBaseException,  Exception;

	public int addChannelTransferRule(Connection con, ChannelTransferRuleVO channelTransferRuleVO) throws BTSLBaseException, Exception;

	public ArrayList loadProductList(Connection con, String networkCode, String c2sModule)throws BTSLBaseException, Exception;

	public ArrayList loadCategoryList(Connection con, String domainCode)throws BTSLBaseException, Exception;

	public Object getListValueVOFromLookupsVO(String uncontrollTxnLevel, String channelTransferLevelSelf)throws BTSLBaseException;
	public ChannelTransferRuleVO requestVOToChangeDAOVO(ChannelTransferRuleRequestVO requestVO);
	public ChannelTransferRuleViewResponseVO responseVOToChangerequestVO(ChannelTransferRuleViewResponseVO response, ChannelTransferRuleVO responseVO);
}
