package com.client.pretups.channel.user.businesslogic;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;

@Path("/usertransfer")
public interface UserTransferRestService {

       /* @POST
        @Path("/load-domain-data")
        @Consumes(value=MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public PretupsResponse<List<ListValueVO>> loadDomainData(String requestData) throws BTSLBaseException , Exception;

                @POST
                @Path("/load-category-data")
                @Consumes(value=MediaType.APPLICATION_JSON)
                @Produces(MediaType.APPLICATION_JSON)
                public PretupsResponse<List<ListValueVO>> loadCategoryData(String requestData) throws BTSLBaseException , Exception;*/

				/**
				 * UserTransferRestService.java
				 * @param requestData
				 * @return
				 * @throws BTSLBaseException
				 * @throws Exception
				 * PretupsResponse<List<ListValueVO>>
				 * akanksha.gupta
				 * 01-Sep-2016 3:05:14 pm
				 */
				@POST
				@Path("/load-category-data")
				@Consumes(value=MediaType.APPLICATION_JSON)
				@Produces(MediaType.APPLICATION_JSON)
				public PretupsResponse<List<ListValueVO>> loadCategoryData(String requestData) throws BTSLBaseException , Exception;

	
                /**
                 * UserTransferRestService.java
                 * @param requestData
                 * @return
                 * @throws BTSLBaseException
                 * @throws Exception
                 * PretupsResponse<List<UserVO>>
                 * akanksha.gupta
                 * 01-Sep-2016 3:05:11 pm
                 */
                @POST
                @Path("/load-user-list")
                @Consumes(value=MediaType.APPLICATION_JSON)
                @Produces(MediaType.APPLICATION_JSON)
                public PretupsResponse<List<UserVO>> loadUserListData(String requestData) throws BTSLBaseException , Exception;

                /**
                 * UserTransferRestService.java
                 * @param requestData
                 * @return
                 * @throws BTSLBaseException
                 * @throws Exception
                 * PretupsResponse<ChannelUserVO>
                 * akanksha.gupta
                 * 01-Sep-2016 3:05:08 pm
                 */
                @POST
                @Path("/confirm-user-details")
                @Consumes(value=MediaType.APPLICATION_JSON)
                @Produces(MediaType.APPLICATION_JSON)
                public PretupsResponse<ChannelUserVO> confirmUserDetail(String requestData) throws BTSLBaseException , Exception;

                /**
                 * UserTransferRestService.java
                 * @param requestData
                 * @return
                 * @throws BTSLBaseException
                 * @throws Exception
                 * PretupsResponse<List<UserVO>>
                 * akanksha.gupta
                 * 01-Sep-2016 3:05:06 pm
                 */
                @POST
                @Path("/load-channel-user-list")
                @Consumes(value=MediaType.APPLICATION_JSON)
                @Produces(MediaType.APPLICATION_JSON)
                public PretupsResponse<List<UserVO>> loadChannelUserListData(String requestData) throws BTSLBaseException , Exception;


                /**
                 * UserTransferRestService.java
                 * @param requestData
                 * @return
                 * @throws BTSLBaseException
                 * @throws Exception
                 * PretupsResponse<List<ChannelUserTransferVO>>
                 * akanksha.gupta
                 * 01-Sep-2016 3:05:02 pm
                 */
                @POST
                @Path("/load-initiated-user-transfer-details")
                @Consumes(value=MediaType.APPLICATION_JSON)
                @Produces(MediaType.APPLICATION_JSON)
                public PretupsResponse<List<ChannelUserTransferVO>> loadInitiatedUserTransferListData(String requestData) throws BTSLBaseException , Exception;

  
                
                /**
                 * UserTransferRestService.java
                 * @param requestData
                 * @return
                 * @throws BTSLBaseException
                 * @throws Exception
                 * PretupsResponse<List<ChannelUserTransferVO>>
                 * akanksha.gupta
                 * 01-Sep-2016 3:04:55 pm
                 */
                @POST
                @Path("/load-initiated-user-with-msisdn")
                @Consumes(value=MediaType.APPLICATION_JSON)
                @Produces(MediaType.APPLICATION_JSON)
                public PretupsResponse<List<ChannelUserTransferVO>> loadInitiatedUserTransferWithMsisdn(String requestData) throws BTSLBaseException , Exception;

  
                
                /**
                 * UserTransferRestService.java
                 * @param requestData
                 * @return
                 * @throws BTSLBaseException
                 * @throws Exception
                 * PretupsResponse<Object>
                 * akanksha.gupta
                 * 01-Sep-2016 3:04:59 pm
                 */
                @POST
                @Path("/confirm-user-transfer")
                @Consumes(value=MediaType.APPLICATION_JSON)
                @Produces(MediaType.APPLICATION_JSON)
                public PretupsResponse<Object> confirmUserTransfer(String requestData) throws BTSLBaseException , Exception;

}
