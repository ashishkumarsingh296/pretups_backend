package channelUserApi.testunit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.db.util.BTSLDBManager;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserQry;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserQry;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OracleUtil;
import com.web.user.businesslogic.UserWebDAO;

@RunWith(MockitoJUnitRunner.class)

public class TestBarUnbarChannelUser {
	
	public static final Log log = LogFactory.getLog(TestBarUnbarChannelUser.class.getName());
	
	
	    @Mock
	    Connection mockConn;
	    @Mock
	    MComConnectionI mockMconConnI;
	    @Mock
	    MComConnection mockMconConn;
	    @Mock
	    PreparedStatement mockPreparedStmnt;
	    @Mock
	    ResultSet mockResultSet;
	    @InjectMocks
	    private OracleUtil oracleUtil;
	    @Mock
        BTSLDBManager bTSLDBManager;
        @Mock
        InitialContext initialContext;
        @Mock
        DataSource dataSource;
	    @Mock
	    BarredUserDAO barredUserDAO ;
	    @Mock
	    UserQry userQry;
	    
	    @Mock
	    UserVO userVO;
	    @Mock
	    UserPhoneVO userPhoneVO;
	    @Mock
	    ErrorMap errorMap;
	    @Mock
	    ChannelUserDAO channelUserDAO;
	    @Mock
	    ChannelUserQry channelUserQry ;
	    @Mock
	    UserWebDAO userWebDAO;
	    @Before
	    public void init() throws SQLException, BTSLBaseException, NamingException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	         assertNotNull(mockMconConn);
	         /*doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
	         when(mockPreparedStmnt.executeQuery()).thenReturn(mockResultSet);
	         //doReturn(userVo).when(userDAO).loadDomainListByUserId(mockConn, "h");
	         //doReturn(userPhoneVO).when(userDAO.loadUserAnyPhoneVO(mockConn, "h"));
	         when(channelUserQry.loadChannelUserDetailsQry()).thenReturn("h");
	         //when(barUnbarUserServiceImpl.vaidateBarUserRequest(mockConn, barUnbarRequestVO, errorMap, userVO))

	         when(userQry.loadUsersDetailsQry()).thenReturn("h");*/
	    }
	    

		/*  @Test public void testCreateWithNoExceptions1() throws Exception {
			  when(mockResultSet.next()).thenReturn(true);
				UserVO userVo = userDAO.loadUsersDetails(mockConn, "h");
			   assertEquals(null, userVo);
			  }*/
		  
		  @Test public void testCreateWithNoExceptions1() throws Exception {
             ChannelUserVO channeluser= channelUserDAO.loadChannelUserDetails(mockConn,"h");
				assertEquals(null, channeluser);
			  } 
		 
		  @Test public void testCreateWithNoExceptions2() throws Exception {
			  ArrayList<UserEventRemarksVO> barUnbarRemarks = new ArrayList<>();
              int count = userWebDAO.insertEventRemark(mockConn, barUnbarRemarks);
				assertEquals(0, count);
			  } 
		  
		  @Test public void testCreateWithNoExceptions3() throws Exception {
			  BarredUserVO barredUserVO = new BarredUserVO();
			  int addCount = barredUserDAO.addBarredUser(mockConn,barredUserVO);
				assertEquals(0, addCount);
			  }
}
