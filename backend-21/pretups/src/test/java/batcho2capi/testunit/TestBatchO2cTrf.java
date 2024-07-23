package batcho2capi.testunit;

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
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.BTSLDBManager;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserQry;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.web.pretups.channel.transfer.businesslogic.O2CBatchWithdrawWebQry;

@RunWith(MockitoJUnitRunner.class)
public class TestBatchO2cTrf {
	
	public static final Log log = LogFactory.getLog(TestBatchO2cTrf.class.getName());
	
	
	  @InjectMocks 
	  UserDAO recorService2;
	    @InjectMocks
	    ProductTypeDAO recordService3;
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
	         when(mockConn.prepareStatement(any(String.class))).thenReturn(mockPreparedStmnt);
	         doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
	         when(mockPreparedStmnt.executeQuery()).thenReturn(mockResultSet);
	    }
	    

		  @Test public void testCreateWithNoExceptions1() throws Exception {
			  when(mockResultSet.next()).thenReturn(true);
			  UserPhoneVO phoneVo=recorService2.loadUserPhoneVO(mockConn, "NGSE0000001713");
			  assertEquals(null, phoneVo.getPinRequired());
			  }
		  
		  @Test public void testCreateWithNoExceptions2() throws Exception {
			  when(mockResultSet.next()).thenReturn(true);
			  try{
				  recorService2.loadUserPhoneVO(null, "NGSE0000001713");
			  }catch(Exception e){
				  assertEquals("error.general.processing", e.getMessage());
			  }
			  } 
		  
		  @Test public void testCreateWithNoExceptions3() throws Exception {
			  when(mockResultSet.next()).thenReturn(true).thenReturn(false);
				ArrayList <C2sBalanceQueryVO>prodList1 =recordService3.getProductsDetails(mockConn);
				assertEquals(null, prodList1.get(0).getProductCode());
			  } 
		  

}
