package batchO2CAppv.testunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
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
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.BTSLDBManager;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferQry;
import com.btsl.pretups.inter.util.VOMSVoucherDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusQry;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserQry;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.web.pretups.channel.transfer.businesslogic.FOCBatchTransferWebDAO;
import com.web.pretups.channel.transfer.businesslogic.FOCBatchTransferWebQry;
import com.web.pretups.channel.transfer.businesslogic.O2CBatchWithdrawWebDAO;
import com.web.pretups.channel.transfer.businesslogic.O2CBatchWithdrawWebQry;

import batcho2capi.testunit.TestO2CBatchWithdraw;

@RunWith(MockitoJUnitRunner.class)
public class TestBulkCommAprroval {
	public static final Log log = LogFactory.getLog(TestO2CBatchWithdraw.class.getName());
	
	
	  @InjectMocks GeographicalDomainDAO recorService;
	  @InjectMocks FOCBatchTransferDAO batchService;
	  @InjectMocks ProcessStatusDAO processService;
	  @InjectMocks FOCBatchTransferWebDAO focBatchTransferWebDAOService;
	 
	  
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
	    ProcessStatusQry mockPreparedStmnt2;
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
      @Mock
      FOCBatchMasterVO focBatchMasterVO;
      
     
      
	    ArrayList localeList = new ArrayList<>();
	    
	    HttpServletRequest request;
	    
	    UserStatusCache usStCa;
	    @Mock
	    UserStatusVO userStatusVO;
	    @Mock
	    FOCBatchTransferQry fOCBatchTransferQry;
	    @Mock
	    UserQry userQry;
	    @Mock
	    ProcessStatusQry processStatusQry;
	    @Mock
	    FOCBatchTransferWebQry fOCBatchTransferWebQry;
	    
	    
	    @Before
	    public void init() throws SQLException, BTSLBaseException, NamingException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	    	 assertNotNull(mockMconConn);
	         when(mockConn.prepareStatement(any(String.class))).thenReturn(mockPreparedStmnt);
	         doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
	         when(mockPreparedStmnt.executeQuery()).thenReturn(mockResultSet);
	         when(mockPreparedStmnt.executeUpdate()).thenReturn(1);
	       //  doReturn(localeList).when(recorService2).loadDomainListByUserId(mockConn, "h");
	        // when(o2CBatchWithdrawWebQry.validateUsersForBatchO2CWithdrawQry("h","h","h")).thenReturn("h");
	      //   when(userQry.loadUsersDetailsQry()).thenReturn("h");
	         localeList.add("en_US");
	         when(fOCBatchTransferQry.loadBatchItemsMapQry("h")).thenReturn("h");
	       //  when(processStatusQry.loadProcessDetailNetworkWiseWithWaitQry()).thenReturn("h");
	      //   when(fOCBatchTransferWebQry.loadBatchFOCMasterDetailsQry("h","h")).thenReturn("h");

	         
	         
	         
	         
	         
	         
	    }
	    
	    
	    @Test public void testCreateWithNoExceptions1() throws Exception {
			  when(mockResultSet.next()).thenReturn(true).thenReturn(false); 
			  LinkedHashMap downloadDataMap = batchService.loadBatchItemsMap( mockConn,"h","h");
			  assertEquals(null,  downloadDataMap.get("_batchDetailId"));
			  }
	    
	    
	    @Test public void testCreateWithNoExceptions2() throws Exception {
			  when(mockResultSet.next()).thenReturn(true).thenReturn(false); 
			  when(mockResultSet.getString("record_count")).thenReturn("2");
			  ProcessStatusVO processStatusVO = processService.loadProcessDetailNetworkWise( mockConn,"h","h");
			  assertEquals(null,  processStatusVO.getProcessID());
			  }

	    
	    
	   
	    
	    
	    @Test public void testCreateWithNoExceptions3() throws Exception {
	    	 
			  when(mockResultSet.next()).thenReturn(true);
			  when(mockResultSet.getTimestamp(anyString())).thenReturn(Timestamp.valueOf("2021-02-25 00:00:00"));
			  long oldlastModified=101;
			  boolean list=batchService.isBatchModified( mockConn,oldlastModified,"h");
			  assertEquals(true, list);
			  }
	    
	    
	    @Test public void testCreateWithNoExceptions4() throws Exception {
	    	 
			//  when(mockResultSet.next()).thenReturn(true);
			  int updated=batchService.updateBatchStatus( mockConn,"h","h","h");
			  assertEquals(1, updated);
			  }
	    
	  
	    
	    
	 
	    
	    
	    
		}
