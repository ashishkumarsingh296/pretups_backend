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
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.BTSLDBManager;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchItemsVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.GetChannelUsersListResponseVo;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserQry;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.web.pretups.channel.transfer.businesslogic.BatchO2CTransferWebDAO;
import com.web.pretups.channel.transfer.businesslogic.BatchO2CTransferWebQry;
import com.web.pretups.channel.transfer.businesslogic.O2CBatchWithdrawWebDAO;

//import regexapi.testunit.TestRegex;


@RunWith(MockitoJUnitRunner.class)
public class TestFetchChannelUsersList {
	

	
	
	public static final Log log = LogFactory.getLog(TestRegex.class.getName());
	
    @InjectMocks
    O2CBatchWithdrawWebDAO o2CBatchWithdrawWebDAO;
    @InjectMocks 
    UserDAO userDao;
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
    

    @Before
    public void init() throws SQLException, BTSLBaseException, NamingException, InstantiationException, IllegalAccessException, ClassNotFoundException {
 
         when(mockConn.prepareStatement(any(String.class))).thenReturn(mockPreparedStmnt);
       
         doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
         when(mockPreparedStmnt.executeQuery()).thenReturn(mockResultSet);
         when(mockResultSet.getString(anyString())).thenReturn("h");
         when(mockResultSet.next()).thenReturn(false);
         when(userQry.loadAllUserDetailsByLoginIDQry()).thenReturn("h");
         when(userQry.getChannelUsersListQry(mockConn,"h","h","h","h","h",false)).thenReturn(mockPreparedStmnt);
         when(mockResultSet.getTimestamp(anyString())).thenReturn(Timestamp.valueOf("2020-11-18 00:00:00"));
        
        // theMock.when(VOMSVoucherDAO::loadDenominationForBulkVoucherDistribution).thenReturn(voucherDenomList);
    }
    
    
   
    
    @Test
    public void testCreateWithNoExceptions1() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    	List<GeographicalDomainVO> domainParentList=geographicalDomainDAO.loadGeoDomainList(mockConn,  "h", "h");
    	GeographicalDomainVO geographicalDomainVO = (GeographicalDomainVO) domainParentList.get(0);
        assertEquals(null,geographicalDomainVO.getCreatedOn());
    }
    
    @Test
    public void testCreateWithNoExceptions2() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    	String userCategoryName=userDao.getCategoryNameFromCatCode(mockConn, "h", null);
    	assertEquals("h", userCategoryName);
    }
    
    @Test
    public void testCreateWithNoExceptions3() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    	String domainName=userDao.getDomainNameOrCode(mockConn, "h", null);
        assertEquals("h", domainName);
    }
    
    

    
    
    //@Test
    public void testCreateWithNoExceptions4() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    	ChannelUserVO channelUserVO = userDao.loadAllUserDetailsByLoginID(mockConn, "h");
        assertEquals(null, channelUserVO.getActivatedOn());
    }
    
    @Test
    public void testCreateWithNoExceptions5() throws Exception {
    	when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    	 GetChannelUsersListResponseVo response=userDao.getChannelUsersList(mockConn,"h","h","h","h","h",false);
        assertEquals(null, response.getStatus());
    }
    
       


}
