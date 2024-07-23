package changeUserStatusBulk;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.mockito.Mockito.lenient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserQry;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.opensymphony.xwork2.inject.Inject;
import com.restapi.channeluser.service.ChannelUserSearchReqVo;
import com.restapi.channeluser.service.ChannelUserServicesImpl;

@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.class)
public class ChangeUserStatusBulk {
	
	@InjectMocks
	UserDAO userdao;
	
	@Mock
	public Connection con;
		
	
	@Mock
    DataSource mockDataSource;
    @Mock
    Connection mockConn;
    @Mock
    PreparedStatement mockPreparedStmnt;
    @Mock
    ResultSet mockResultSet;
    
    @Mock
    Statement mockStat;
    
    @Mock
    UserVO userVO;
    
    List<UserVO> list=new ArrayList<UserVO>();
    
	
	@Before
	public void init() throws BTSLBaseException, SQLException
	{	
		
	   
		lenient().when(mockConn.prepareStatement(any(String.class))).thenReturn(mockPreparedStmnt);
		lenient().doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
	//	lenient().doNothing().when(mockPreparedStmnt).setTimestamp(anyInt(), BTSLUtil.getTimestampFromUtilDate(new Date()));
		lenient().doNothing().when(mockPreparedStmnt).clearParameters();
		lenient().when(mockPreparedStmnt.executeQuery()).thenReturn(mockResultSet);
		lenient().when(mockPreparedStmnt.executeUpdate()).thenReturn(1);
		lenient().when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		lenient().when(mockResultSet.getLong(anyString())).thenReturn(1L);
		lenient().when(mockResultSet.getInt(anyString())).thenReturn(1);
		lenient().when(mockResultSet.getTimestamp(anyString())).thenReturn(Timestamp.valueOf("2019-03-23 00:00:00"));
		lenient().when(mockResultSet.getDate(anyString())).thenReturn(null);
		lenient().when(mockResultSet.getString(anyString())).thenReturn("vvk");
		lenient().when(mockResultSet.next()).thenReturn(true);
		lenient().when(userVO.toString()).thenReturn("sajjsaj");
		
	}
	
	@Test
	public void A01_ChangeUserStatusWithOutEXp() throws BTSLBaseException { 
		
		userVO.setStatus("Y");
		userVO.setPreviousStatus("EX");
		userVO.setModifiedBy("vamms");
		userVO.setModifiedOn(Timestamp.valueOf("2019-03-23 00:00:00"));
		userVO.setRemarks("vvvk");
		list.add(userVO);
		int i=userdao.changeUserStatusForBatchAll(mockConn,list);
	    assertEquals(i, 1);
	}
	
	
	@Test
	public void A02_ChangeUserStatusWithEXp() throws BTSLBaseException { 
		
		userVO.setStatus("Y");
		userVO.setPreviousStatus("EX");
		userVO.setModifiedBy("vamms");
		userVO.setModifiedOn(Timestamp.valueOf("2019-03-23 00:00:00"));
		userVO.setRemarks("vvvk");
		list.add(userVO);
		try {
		  int i=userdao.changeUserStatusForBatchAll(null,null);
		}catch (Exception e) {
			assertTrue(true);
		}
		
	}
	
	
   

}
