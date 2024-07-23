package channelUserTransfer.testunit;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.UserOtpDAO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.user.businesslogic.UserDAO;
import com.web.pretups.channel.user.businesslogic.ChannelUserTransferWebDAO;

//import testPinChange.TestUserPinChange;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TestChannelUserTransfer {
	public static final Log log = LogFactory.getLog(TestChannelUserTransfer.class.getName());
	 @Mock
	    Connection mockConn;
	    @Mock
	    PreparedStatement mockPreparedStmnt;
	    @Mock
	    ResultSet mockResultSet;
	    @Mock
	    ChannelUserTransferVO TestChannelUserTransfer;
	
	    @InjectMocks
	    ChannelUserTransferWebDAO channelUserTransferwebDAO;
	  
	    @Before
	    public void init() throws SQLException, BTSLBaseException, NamingException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	         when(mockConn.prepareStatement(any(String.class))).thenReturn(mockPreparedStmnt);
	         doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
	         doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
	         when(mockPreparedStmnt.executeQuery()).thenReturn(mockResultSet);
	         when(mockResultSet.next()).thenReturn(true);
	         when(mockResultSet.getString(anyString())).thenReturn("pass");
	 }
	 
@Test
public void testCreateWithNoException1() throws Exception{
	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
	ChannelUserTransferVO channelUserTransferVO =new ChannelUserTransferVO();
	int count = channelUserTransferwebDAO.transferChannelUser(mockConn, channelUserTransferVO);
	assertEquals(count, 0);
}
@Test
public void testCreateWithNoException2() throws Exception{
	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
	int update =channelUserTransferwebDAO.transferChannelUserFinal(mockConn,"a",new ChannelUserTransferVO(),new HashMap<>());
	assertEquals(update, 0);
}



}
