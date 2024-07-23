package batcho2capi.testunit;

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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Locale;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.BTSLDBManager;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserQry;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.web.pretups.channel.transfer.businesslogic.O2CBatchWithdrawWebDAO;
import com.web.pretups.channel.transfer.businesslogic.O2CBatchWithdrawWebQry;

@RunWith(MockitoJUnitRunner.class)
public class TestO2CBatchWithdraw {
	
	public static final Log log = LogFactory.getLog(TestO2CBatchWithdraw.class.getName());
	
	
	  @InjectMocks GeographicalDomainDAO recorService;
	  @Spy
	 @InjectMocks 
	  UserDAO recorService2;
	    @InjectMocks
	    O2CBatchWithdrawWebDAO recorService1;
	    @Mock
	    DataSource mockDataSource;
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
        Constants constants;
	    ArrayList localeList = new ArrayList<>();
	    
	    HttpServletRequest request;
	    
	    UserStatusCache usStCa;
	    @Mock
	    UserStatusVO userStatusVO;
	    @Mock
	    O2CBatchWithdrawWebQry o2CBatchWithdrawWebQry;
	    @Mock
	    UserQry userQry;
	    
	    @Before
	    public void init() throws SQLException, BTSLBaseException, NamingException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	    	 assertNotNull(mockMconConn);
	         when(mockConn.prepareStatement(any(String.class))).thenReturn(mockPreparedStmnt);
	         doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
	         when(mockPreparedStmnt.executeQuery()).thenReturn(mockResultSet);
	         doReturn(localeList).when(recorService2).loadDomainListByUserId(mockConn, "h");
	         when(o2CBatchWithdrawWebQry.validateUsersForBatchO2CWithdrawQry("h","h","h")).thenReturn("h");
	         when(userQry.loadUsersDetailsQry()).thenReturn("h");
	         localeList.add("en_US");
	    }
	    
		  @Test public void testCreateWithNoExceptions1() throws Exception {
		  when(mockResultSet.next()).thenReturn(true);
		  boolean list=recorService.isGeographicalDomainExist( mockConn,anyString(),true);
		  assertEquals(true, list);
		  }
		  
		  @Test public void testCreateWithNoExceptions2() throws Exception {
		  when(mockResultSet.next()).thenReturn(true).thenReturn(false); 
		  ArrayList list=recorService.loadUserGeographyList( mockConn,"h","h");
		  assertEquals(null, ((UserGeographiesVO)list.get(0)).getNetworkName());
		  }
		  
		  @Test public void testCreateWithNoExceptions3() throws Exception {
			  when(mockResultSet.next()).thenReturn(true); 
			  ArrayList batchArrayList = new ArrayList<>(); 
			  Locale locale=new Locale("h", "h"); 
			  ArrayList list=recorService1.validateUsersForBatchO2CWithdraw(
			  mockConn,batchArrayList,"mock",",mock","mock","mock",null,locale);
			  assertEquals(new ArrayList<>(), list); 
			  }
			
			
		  @Test public void testCreateWithNoExceptions4() throws Exception {
		  when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		  when(mockResultSet.getTimestamp(anyString())).thenReturn(Timestamp.valueOf("2019-03-23 00:00:00"));
		  UserVO list=recorService2.loadUsersDetails( mockConn,"724345345");
		  assertEquals(null, list.getActiveUserID()); 
		  }
		 

}
