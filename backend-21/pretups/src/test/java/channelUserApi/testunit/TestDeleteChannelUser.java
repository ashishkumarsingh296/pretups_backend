package channelUserApi.testunit;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.BTSLDBManager;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.LookupsDAO;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.user.requesthandler.ChannelSOSSettlementHandler;
import com.btsl.user.businesslogic.GetChannelUsersListResponseVo;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserQry;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.web.pretups.channel.transfer.businesslogic.BatchO2CTransferWebDAO;
import com.web.pretups.channel.transfer.businesslogic.BatchO2CTransferWebQry;
import com.web.pretups.channel.transfer.businesslogic.O2CBatchWithdrawWebDAO;

//import regexapi.testunit.TestRegex;


@RunWith(MockitoJUnitRunner.class)

public class TestDeleteChannelUser {
	

	
	
	public static final Log log = LogFactory.getLog(TestRegex.class.getName());
	
    @InjectMocks
    FOCBatchTransferDAO batchTransferDAO ;
    @InjectMocks 
    UserDAO userDao;
    @InjectMocks 
    LookupsDAO lookupsDAO;
   @InjectMocks 
   ChannelSOSSettlementHandler channelSOSSettlementHandler ;
   @InjectMocks 
   UserTransferCountsDAO userTrfCntDAO ;
   @InjectMocks 
   ChannelTransferDAO transferDAO ;
   @InjectMocks 
   RestrictedSubscriberDAO restrictedSubscriberDAO;
   @InjectMocks 
   UserBalancesDAO userBalancesDAO;
    
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
    @InjectMocks 
    GeographicalDomainDAO geographicalDomainDAO; 
    @Mock
    UserQry userQry;
    @Mock
    CategoryVO categoryVO;
    

    @Before
    public void init() throws SQLException, BTSLBaseException, NamingException, InstantiationException, IllegalAccessException, ClassNotFoundException {
 
         when(mockConn.prepareStatement(any(String.class))).thenReturn(mockPreparedStmnt);
       
         doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
         when(mockPreparedStmnt.executeQuery()).thenReturn(mockResultSet);
         when(mockResultSet.getString(anyString())).thenReturn("h");
         when(mockResultSet.next()).thenReturn(false);
         when(userQry.loadUserDetailsByMsisdnQry()).thenReturn("h");
         when(userQry.loadAllUserDetailsByLoginIDQry()).thenReturn("h");
         when(userQry.isChildUserActiveQry()).thenReturn("h");
         when(userQry.getChannelUsersListQry(mockConn,"h","h","h","h","h",false)).thenReturn(mockPreparedStmnt);
         when(mockResultSet.getTimestamp(anyString())).thenReturn(Timestamp.valueOf("2020-11-18 00:00:00"));
        
        // theMock.when(VOMSVoucherDAO::loadDenominationForBulkVoucherDistribution).thenReturn(voucherDenomList);
    }
    
    
   
    
    //@Test
    public void testCreateWithNoExceptions1() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    	UserVO sessionUserVO = (UserVO) userDao.loadAllUserDetailsByLoginID(mockConn,"h");
    	
        assertEquals("h",sessionUserVO.getStatus());
    }
    
    @Test
    public void testCreateWithNoExceptions2() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    	
    	 
          boolean isO2CPendingFlag = transferDAO.isPendingTransactionExist(mockConn,"h");
        assertEquals(true, isO2CPendingFlag);
    }
    
    @Test
    public void testCreateWithNoExceptions3() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    	String domainName=userDao.getDomainNameOrCode(mockConn, "h", null);
    	 ArrayList lookupList=lookupsDAO.loadLookupsFromLookupCode(mockConn, "h","h");
    	 ArrayList dummyList= new ArrayList<>();
        assertEquals(dummyList, lookupList);
    }
    
    

    
    
    @Test
    public void testCreateWithNoExceptions4() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    
    	final boolean isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(mockConn, "h");
        assertEquals(true,isSOSPendingFlag);
    }
    
    @Test
    public void testCreateWithNoExceptions5() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    	  final boolean isChildFlag = userDao.isChildUserActive(mockConn, "h");
        assertEquals(true, isChildFlag);
    }
    
    
    @Test
    public void testCreateWithNoExceptions6() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    	
    	UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
    	userTrfCntVO = userTrfCntDAO.selectLastSOSTxnID("h", mockConn, false, null);
        assertEquals(null, userTrfCntVO.getCategory());
    }
    
    @Test
    public void testCreateWithNoExceptions7() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    	
    	UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
		userTrfCntVO = userTrfCntDAO.selectLastLRTxnID("h", mockConn, false, null);
        assertEquals(null, userTrfCntVO.getCategory());
    }
    
    
    @Test
    public void testCreateWithNoExceptions8() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
       boolean isbatchFocPendingTxn = batchTransferDAO.isPendingTransactionExist(mockConn,"h");
        assertEquals(true, isbatchFocPendingTxn);
    }
    
    @Test
    public void testCreateWithNoExceptions9() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        boolean isRestrictedMsisdnFlag = restrictedSubscriberDAO.isSubscriberExistByChannelUser(mockConn,"h");
        
        assertEquals(true, isRestrictedMsisdnFlag);
    }
    
    
    @Test
    public void testCreateWithNoExceptions10() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        boolean  isBalanceFlag = userDao.isUserBalanceExist(mockConn,"h");
        
        assertEquals(false, isBalanceFlag);
    }
    
    
    
    
    @Test
    public void testCreateWithNoExceptions11() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    	 ArrayList<UserBalancesVO> userBal  = userBalancesDAO.loadUserBalanceForDelete(mockConn,"h");
    	 UserBalancesVO userBalancesVO = (UserBalancesVO) userBal.get(0);  
        assertEquals(null, userBalancesVO.getUserID());
    }
    
    
    @Test
    public void testCreateWithNoExceptions12() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    	 ArrayList dummyList= new ArrayList<>();
    	int deleteCount = userDao.deleteSuspendUser(mockConn,dummyList);
    	 
        assertEquals(0, deleteCount);
    }
    
    
   
    
    
    
    
   
    
    
   
    
    
       


}
