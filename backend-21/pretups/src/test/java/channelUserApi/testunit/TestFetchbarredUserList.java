package channelUserApi.testunit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import com.btsl.common.ListValueVO;
import com.btsl.db.util.BTSLDBManager;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserQry;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OracleUtil;
import com.restapi.user.service.BarredVo;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

@RunWith(MockitoJUnitRunner.class)

public class TestFetchbarredUserList {
	
	public static final Log log = LogFactory.getLog(TestFetchbarredUserList.class.getName());
	
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
	    ChannelUserWebDAO channelUserWebDAO;
	    @Mock
	    UserWebDAO userWebDAO;
	    @Mock
	    BarredVo barredVo;
	    @Before
	    public void init() throws SQLException, BTSLBaseException, NamingException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	         assertNotNull(mockMconConn);
	    }
	    
	
		  @Test public void testCreateWithNoExceptions1() throws Exception {
			  ArrayList<BarredVo> barredUserList = barredUserDAO.fetchBarredUserList(mockConn, barredVo);
				assertEquals(new ArrayList<BarredVo>(), barredUserList);
			  } 
	
		  @Test public void testCreateWithNoExceptions2() throws Exception {
	            ArrayList<ListValueVO> childUserList = userWebDAO.loadUserListByLogin(mockConn, "h", PretupsI.STAFF_USER_TYPE, "%"); 
				assertEquals(new ArrayList<>(), childUserList);

			  } 
		 
		  @Test public void testCreateWithNoExceptions3() throws Exception {
	               ArrayList<ChannelUserVO> hierarchyList = channelUserWebDAO.loadChannelUserHierarchy(mockConn, "h", false);
					assertEquals(new ArrayList<BarredVo>(), hierarchyList);
			  }
		  
}
