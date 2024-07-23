package batchUserInititate;

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
import java.util.Locale;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.BTSLDBManager;
import com.btsl.db.util.MComConnection;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.user.businesslogic.BatchUserDAO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayForCategoryCache;
import com.btsl.pretups.master.businesslogic.GeographicalDomainTypeVO;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
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
import com.btsl.util.OracleUtil;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductQry;
import com.web.pretups.channel.user.businesslogic.BatchUserWebDAO;
import com.web.pretups.channel.user.businesslogic.BatchUserWebQry;
import com.web.pretups.user.businesslogic.ChannelUserWebQry;
import com.web.user.businesslogic.UserWebDAO;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TestBatchUserInitiate {
	
	public static final Log log = LogFactory.getLog(TestBatchUserInitiate.class.getName());
	

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
	BatchUserWebQry batchUserWebQry;
	@Mock
	ChannelUserWebQry channelUserWebQry;
	@InjectMocks
	ChannelUserDAO channelUserDAO;
	@InjectMocks
	UserRolesDAO userRolesDAO;
	@InjectMocks
	SubLookUpDAO sublookupDAO;
	@InjectMocks
	BatchUserWebDAO batchUserWebDAO;
	@InjectMocks
	BatchUserDAO batchUserDAO;
	@Before
	public void init() throws SQLException {
		when(mockConn.prepareStatement(any(String.class))).thenReturn(mockPreparedStmnt);
		doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
		when(mockPreparedStmnt.executeQuery()).thenReturn(mockResultSet);
		when(mockResultSet.next()).thenReturn(false);
		when(userQry.loadAllUserDetailsByLoginIDQry()).thenReturn("h");
		when(channelUserWebQry.loadParentUserDetailsByUserID()).thenReturn("h");
		when(batchUserWebQry.loadMasterGeographyListQry()).thenReturn("h");
		when(batchUserWebQry.loadCommProfileListQry(anyString(), anyString())).thenReturn("h");
		
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
		ArrayList<ListValueVO> data = sublookupDAO.loadSublookupByLookupType(mockConn, PretupsI.OUTLET_TYPE);
		assertEquals("Priyank" , data.get(0).getLabel());
	}
	
	@Test
	public void testCreateWithNoExceptions3() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		ArrayList<ListValueVO> data = servicesDAO.loadServicesList(mockConn, "h", PretupsI.C2S_MODULE, null, false);
		assertEquals("Priyank" , data.get(0).getLabel());
	}
	
	@Test
	public void testCreateWithNoExceptions4() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		ArrayList<UserGeographiesVO> data = batchUserWebDAO.loadMasterGeographyList(mockConn, "h", "h");
		assertEquals("Priyank" , data.get(0).getGraphDomainCode());
	}
	
	@Test
	public void testCreateWithNoExceptions5() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		ArrayList<GeographicalDomainTypeVO> data = batchUserWebDAO.loadCategoryGeographyTypeList(mockConn, "h");
		assertEquals("Priyank" , data.get(0).getCategoryCode());
	}
	
	@Test
	public void testCreateWithNoExceptions6() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		ArrayList<ChannelTransferRuleVO> data = batchUserWebDAO.loadMasterCategoryHierarchyList(mockConn, "h", "h");
		assertEquals("Priyank" , data.get(0).getFromCategory());
	}
	
	@Test
	public void testCreateWithNoExceptions7() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		ArrayList<CategoryVO> data = batchUserDAO.loadMasterCategoryList(mockConn, "h", "h", "h");
		assertEquals("Priyank" , data.get(0).getCategoryCode());
	}
	
	@Test
	public void testCreateWithNoExceptions8() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		ArrayList<UserRolesVO> data = batchUserDAO.loadMasterGroupRoleList(mockConn, "h", "h", "h");
		assertEquals("Priyank" , data.get(0).getCategoryCode());
	}
	
	@Test
	public void testCreateWithNoExceptions9() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		HashMap data = batchUserDAO.loadLanguageList(mockConn);
		assertEquals("Priyank" , data.get("Priyank_Priyank"));
	}
	
	@Test
	public void testCreateWithNoExceptions10() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		ArrayList<GradeVO> data = batchUserDAO.loadMasterCategoryGradeList(mockConn, "h", "h", "h");
		assertEquals("Priyank" , data.get(0).getCategoryCode());
	}
	
/*	@Test
	public void testCreateWithNoExceptions11() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		ArrayList<TransferProfileVO> data = batchUserDAO.loadMasterTransferProfileList(mockConn, "h", "h", "h", "h");
		assertEquals("Priyank" , data.get(0).getCategory());
	}*/
	
	@Test
	public void testCreateWithNoExceptions12() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		ArrayList<CommissionProfileSetVO> data = batchUserWebDAO.loadCommProfileList(mockConn, "h", "h", "h", "h");
		assertEquals("Priyank" , data.get(0).getCategoryCode());
	}
	
	@Test
	public void testCreateWithNoExceptions13() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		boolean data = userDAO.isUserLoginExist(mockConn, "h", null);
		assertEquals(true , data);
	}
	
	@Test
	public void testCreateWithNoExceptions14() throws Exception {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		boolean data = userDAO.isMSISDNExist(mockConn, "h", null);
		assertEquals(true , data);
	}
	
	

}
