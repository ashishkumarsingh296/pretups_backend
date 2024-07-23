package areaSearch;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
//import org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
//import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.junit.MockitoJUnitRunner;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainTypeVO;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserQry;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserQry;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.Constants;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebQry;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TestAreaSearch {

	public static final Log log = LogFactory.getLog(TestAreaSearch.class.getName());

	@InjectMocks
	ServicesTypeDAO servicesDAO;
	@InjectMocks
	UserDAO userDAO;
	@Mock
	Connection mockConn;
	@Mock
	PreparedStatement mockPreparedStmnt;
	@Mock
	ResultSet mockResultSet;
	@Mock
	Constants constants;
	@Mock
	UserQry userQry;
	@Mock
	ChannelUserQry channelUserQry;
	@Mock
	ChannelUserWebQry channelUserWebQry;
	@InjectMocks
	ChannelUserDAO channelUserDAO;
	@InjectMocks
	UserRolesDAO userRolesDAO;
	@InjectMocks
	CategoryDAO categoryDAO;
	@InjectMocks
	CategoryWebDAO categoryWebDAO;
	@InjectMocks
	GeographicalDomainWebDAO geographicalDomainWebDAO;
	@InjectMocks
	GeographicalDomainDAO geographyDAO;
	
	@Before
	public void init() throws SQLException {
		when(mockConn.prepareStatement(any(String.class))).thenReturn(mockPreparedStmnt);
		doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
		when(mockPreparedStmnt.executeQuery()).thenReturn(mockResultSet);
		when(mockResultSet.next()).thenReturn(false);
		when(userQry.loadAllUserDetailsByLoginIDQry()).thenReturn("h");
		when(channelUserWebQry.loadParentUserDetailsByUserID()).thenReturn("h");
		when(mockPreparedStmnt.executeUpdate()).thenReturn(10);
        Mockito.lenient().when(mockResultSet.getString(anyString())).thenReturn("Priyank");
	}
	
	
	//@Test
	public void testCreateWithNoExceptions1() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		UserVO data = userDAO.loadAllUserDetailsByLoginID( mockConn, "h");
		assertEquals("Priyank" , data.getStatus());
	}
	
	@Test
	public void testCreateWithNoExceptions2() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		@SuppressWarnings("unchecked")
		ArrayList<CategoryVO> data = categoryDAO.loadOtherCategorList( mockConn, "h");
		assertEquals("Priyank" , data.get(0).getCategoryCode());
	}
	
	@Test
	public void testCreateWithNoExceptions3() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		@SuppressWarnings("unchecked")
		ArrayList<CategoryVO> data = categoryWebDAO.loadCategorListByDomainCode( mockConn, "h");
		assertEquals("Priyank" , data.get(0).getCategoryCode());
	}
	
	@Test
	public void testCreateWithNoExceptions4() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		@SuppressWarnings("unchecked")
		ArrayList<UserGeographiesVO> data = geographicalDomainWebDAO.loadGeographyList(mockConn, "h", "h", "h");
		assertEquals("Priyank" , data.get(0).getGraphDomainCode());
	}
	
	@Test
	public void testCreateWithNoExceptions5() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		@SuppressWarnings("unchecked")
		ArrayList<GeographicalDomainTypeVO> data = geographicalDomainWebDAO.loadDomainTypes(mockConn, 10, 10);
		assertEquals("Priyank" , data.get(0).getGrphDomainType());
	}
	
	@Test
	public void testCreateWithNoExceptions6() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		@SuppressWarnings("unchecked")
		ArrayList<UserGeographiesVO> data = geographyDAO.loadUserGeographyList(mockConn, "h", "h");
		assertEquals("Priyank" , data.get(0).getGraphDomainCode());
	}
	
	
	

}