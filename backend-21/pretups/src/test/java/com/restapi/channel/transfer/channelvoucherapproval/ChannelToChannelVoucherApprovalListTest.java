package com.restapi.channel.transfer.channelvoucherapproval;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.JUnitConfig;
import org.junit.Ignore;
import org.junit.Test;


import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChannelToChannelVoucherApprovalListTest {
    /**
     * Method under test: {@link ChannelToChannelVoucherApprovalList#process(RequestVO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testProcess() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        ChannelToChannelVoucherApprovalList channelToChannelVoucherApprovalList = new ChannelToChannelVoucherApprovalList();

        RequestVO p_requestVO = new RequestVO();
        channelToChannelVoucherApprovalList.process(p_requestVO);
    }

    /**
     * Method under test: {@link ChannelToChannelVoucherApprovalList#setSearchUserInfo(ChannelUserVO, ChannelUserVO, String)}
     */


    @Test
    ////@Ignore("TODO: Complete this test")
    public void testSetSearchUserInfo() throws BTSLBaseException {
        //  com.btsl.util.JUnitConfig.init(); //Auto replace
        JUnitConfig.init();
        ChannelToChannelVoucherApprovalList channelToChannelVoucherApprovalList = new ChannelToChannelVoucherApprovalList();
        ChannelUserVO searchUserVO = mock(ChannelUserVO.class);//ChannelUserVO.getInstance();
        ChannelUserVO searchUserVO2 = mock(ChannelUserVO.class);//ChannelUserVO.getInstance();

        ArrayList domainList = new ArrayList();
        ListValueVO domainVO = new ListValueVO();
        domainVO.setTypeName("Test Domain");
        domainVO.setCodeName("Test Domain Code");
        domainVO.setStatus("Y");
        domainVO.setType("Test");
        domainList.add(domainVO);
        when(searchUserVO2.getDomainList()).thenReturn(domainList);
        when(searchUserVO2.getCategoryVO()).thenReturn(new CategoryVO());

        channelToChannelVoucherApprovalList.setSearchUserInfo(searchUserVO, searchUserVO2, "All User");
    }

    /**
     * Method under test: {@link ChannelToChannelVoucherApprovalList#setSearchUserInfo(ChannelUserVO, ChannelUserVO, String)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testSetSearchUserInfo2() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        ChannelToChannelVoucherApprovalList channelToChannelVoucherApprovalList = new ChannelToChannelVoucherApprovalList();
        ChannelUserVO searchUserVO = mock(ChannelUserVO.class);
        ChannelUserVO searchUserVO2 = mock(ChannelUserVO.class);


        ArrayList domainList = new ArrayList();
        ListValueVO domainVO = new ListValueVO();
        domainVO.setTypeName("Test Domain");
        domainVO.setCodeName("Test Domain Code");
        domainVO.setStatus("Y");
        domainVO.setType("Test");

        domainList.add(domainVO);
        when(searchUserVO2.getDomainList()).thenReturn(domainList);
        when(searchUserVO2.getCategoryVO()).thenReturn(new CategoryVO());


        channelToChannelVoucherApprovalList.setSearchUserInfo(searchUserVO, searchUserVO2, "All User");
    }

    /**
     * Method under test: {@link ChannelToChannelVoucherApprovalList#setSearchUserInfo(ChannelUserVO, ChannelUserVO, String)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testSetSearchUserInfo3() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace

        ChannelToChannelVoucherApprovalList channelToChannelVoucherApprovalList = new ChannelToChannelVoucherApprovalList();
        ChannelUserVO searchUserVO = mock(ChannelUserVO.class);
        ChannelUserVO searchUserVO2 = mock(ChannelUserVO.class);


        ArrayList domainList = new ArrayList();
        ListValueVO domainVO = new ListValueVO();
        domainVO.setTypeName("Test Domain");
        domainVO.setCodeName("Test Domain Code");
        domainVO.setStatus("Y");
        domainVO.setType("Test");

        domainList.add(domainVO);
        when(searchUserVO2.getDomainList()).thenReturn(domainList);
        when(searchUserVO2.getCategoryVO()).thenReturn(new CategoryVO());


        channelToChannelVoucherApprovalList.setSearchUserInfo(searchUserVO, searchUserVO2, "All User");
    }

    /**
     * Method under test: {@link ChannelToChannelVoucherApprovalList#loadTransferApprovalListLevelN(String, Boolean, String, String, String, String, ChannelUserVO, RequestVO, String, String, String, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadTransferApprovalListLevelN() throws BTSLBaseException {
        // TODO: Complete this test.
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.channel.transfer.channelvoucherapproval.ChannelToChannelVoucherApprovalList.loadTransferApprovalListLevelN(ChannelToChannelVoucherApprovalList.java:651)

        // Arrange
        // TODO: Populate arranged inputs
        ChannelToChannelVoucherApprovalList channelToChannelVoucherApprovalList = null;
        String transactionId = "";
        Boolean fromUserCodeFlag = null;
        String approvalLevel = "";
        String allOrder = "";
        String allUser = "";
        String transferSubType = "";
        ChannelUserVO searchUserVO = null;
        RequestVO p_requestVO = null;
        String pageNumber = "";
        String entriesPerPage = "";
        String userNameSearch = "";
        String requestType = "";

        // Act
        ArrayList<ChannelTransferVO> actualLoadTransferApprovalListLevelNResult = channelToChannelVoucherApprovalList
                .loadTransferApprovalListLevelN(transactionId, fromUserCodeFlag, approvalLevel, allOrder, allUser,
                        transferSubType, searchUserVO, p_requestVO, pageNumber, entriesPerPage, userNameSearch, requestType);

        // Assert
        // TODO: Add assertions on result
    }
}

