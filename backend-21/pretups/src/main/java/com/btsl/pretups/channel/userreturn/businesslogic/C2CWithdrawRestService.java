package com.btsl.pretups.channel.userreturn.businesslogic;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.userreturn.web.C2CWithdrawVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;

@Path("/greetings")
public interface C2CWithdrawRestService {

		@POST
		@Path("/load-cat-list-by-domain")
		@Consumes(value=MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public PretupsResponse<List> loadCatListByByDomain(String requestData) throws BTSLBaseException, Exception ;
		
		@POST
		@Path("/load-user-list")
		@Consumes(value=MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public PretupsResponse<List<UserVO>> loadUserListData(String requestData) throws BTSLBaseException, Exception ;
		
		@POST
		@Path("/load-user-details")   //load channel user details
		@Consumes(value=MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public PretupsResponse<ChannelUserVO> loadUserListDetail(String requestData) throws BTSLBaseException, Exception ;
		
		@POST
		@Path("/load-channel-user-list")
		@Consumes(value=MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public PretupsResponse<List<UserVO>> loadChannelUserListData(String requestData) throws BTSLBaseException, Exception ;
		
		@POST
		@Path("/load-category-list-by-transfer-rule")
		@Consumes(value=MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public PretupsResponse<List> loadCatListByTransferRule(String requestData) throws BTSLBaseException, Exception ;
		
		@POST
		@Path("/load-channel-user-list-sender")
		@Consumes(value=MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public PretupsResponse<List<ListValueVO>> loadChannelUserListSender(String requestData) throws BTSLBaseException, Exception ;
		
		@POST
		@Path("/validate-channel-user")
		@Consumes(value=MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public PretupsResponse<C2CWithdrawVO> validateChannelUser(String requestData) throws Exception;
		
		@POST
		@Path("/confirm-transaction")
		@Consumes(value=MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public PretupsResponse<ChannelTransferItemsVO> confirmC2CWithdraw(String requestData) throws Exception;

		@POST
		@Path("/confirm-withdraw")
		@Consumes(value=MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public PretupsResponse<ChannelTransferVO> confirmTransaction(String requestData) throws Exception ;
}
