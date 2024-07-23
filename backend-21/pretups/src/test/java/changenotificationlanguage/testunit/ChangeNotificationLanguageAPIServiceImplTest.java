package changenotificationlanguage.testunit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.restapi.channeluser.service.ChangeNotificationLanguageAPIServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class ChangeNotificationLanguageAPIServiceImplTest {
//	

	public static final Log log = LogFactory.getLog(ChangeNotificationLanguageAPIServiceImplTest.class.getName());

	@InjectMocks
	ChangeNotificationLanguageAPIServiceImpl changeNotificationLanguageAPIServiceImpl;

	@Mock
	Connection mockConn;


	@InjectMocks
	UserDAO userDAO;

	@Mock
	ChannelUserDAO channelUserDAO;


	@Test
	public void filterUserPhoneListTest1() {

		ArrayList msisdnList = new ArrayList();
		ChannelUserVO channelUserVO = new ChannelUserVO();
		channelUserVO.setMsisdn("72525252");
		msisdnList.add(channelUserVO);
		ArrayList filteredMsisdnList = changeNotificationLanguageAPIServiceImpl.filterUserPhoneList(msisdnList,
				"72525252");
		assertEquals(filteredMsisdnList.size(), 1);
	}

	@Test
	public void filterUserPhoneListTest2() {

		ArrayList msisdnList = new ArrayList();
		ChannelUserVO channelUserVO = new ChannelUserVO();
		channelUserVO.setUserPhoneVO(new UserPhoneVO());

		channelUserVO.getUserPhoneVO().setMsisdn("72525252");
		msisdnList.add(channelUserVO);

		channelUserVO = new ChannelUserVO();
		channelUserVO.setUserPhoneVO(new UserPhoneVO());
		channelUserVO.getUserPhoneVO().setMsisdn("723000000");
		msisdnList.add(channelUserVO);

		String userMsisdn = "72525252";
		ArrayList filteredMsisdnList = changeNotificationLanguageAPIServiceImpl.filterUserPhoneList(msisdnList,
				userMsisdn);
		String filteredMsisdn = ((ChannelUserVO) filteredMsisdnList.get(0)).getUserPhoneVO().getMsisdn();

		assertEquals(filteredMsisdn, userMsisdn);
	}

	@Test
	public void loadLanguageListTest1() {

		try {
			ArrayList languageListVO = new ArrayList();
			languageListVO.add("xyz");
			when(channelUserDAO.loadLanguageListForUser(mockConn)).thenReturn(languageListVO);

			ArrayList finalLanguageList;
			finalLanguageList = changeNotificationLanguageAPIServiceImpl.loadLanguageList(mockConn, channelUserDAO);

			assertEquals(finalLanguageList, languageListVO);
		} catch (BTSLBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Test
	public void loadLanguageListTest2() {

		ArrayList languageListVO = new ArrayList();
		languageListVO.add("en");
		ArrayList finalLanguageList;
		
		try {
			when(channelUserDAO.loadLanguageListForUser(mockConn)).thenReturn(languageListVO);

			
			finalLanguageList = changeNotificationLanguageAPIServiceImpl.loadLanguageList(mockConn, channelUserDAO);
			assertEquals(finalLanguageList, languageListVO);
			
		} catch (BTSLBaseException e) {

			
			e.printStackTrace();
		}

	}

}
