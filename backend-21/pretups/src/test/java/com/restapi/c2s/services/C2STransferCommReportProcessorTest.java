package com.restapi.c2s.services;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommReqDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.util.JUnitConfig;
import com.btsl.util.OracleUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {C2STransferCommReportProcessor.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class C2STransferCommReportProcessorTest {
    @Autowired
    private C2STransferCommReportProcessor c2STransferCommReportProcessor;

    /**
     * Method under test: {@link C2STransferCommReportProcessor#execute(C2STransferCommReqDTO, C2STransferCommDownloadResp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testExecute() throws BTSLBaseException {
        JUnitConfig.init();
       // Mockito.mockStatic(OracleUtil.class) ;

       /* try (MockedConstruction<MComConnection> mComm = Mockito.mockConstruction(MComConnection.class,
                (mock, context) -> {
                    // further stubbings ...
                    when(mock.getConnection()).thenReturn(JUnitConfig.getConnection());
                })) {
            MComConnection MComm = new MComConnection();
            mockStatic(OracleUtil.class);
            JUnitConfig.initConnections();
            when(OracleUtil.getConnection()).thenReturn(JUnitConfig.getConnection());


        }*/
/*

        try {
            MComConnection MComm = new MComConnection();
            Connection conn = MComm.getConnection();
            PreparedStatement stmt = conn.prepareStatement("TEST");
            ResultSet rset = stmt.executeQuery();
            while (rset.next()) {
                System.out.println("Test");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
*/

        /*
        try {
            Mockito.when(OracleUtil.getConnection()).thenReturn(JUnitConfig.getMComConnection().getConnection());
        }catch(Exception e){
            e.printStackTrace();

        }
*/

        C2STransferCommReqDTO c2STransferCommReqDTO = new C2STransferCommReqDTO();
        c2STransferCommReqDTO.setUserType("TEST");
        c2STransferCommReqDTO.setCategoryCode("DIST");
        c2STransferCommReqDTO.setDomain("DIST");
        c2STransferCommReqDTO.setReportDate("27/07/23");
        c2STransferCommReqDTO.setAllowedFromTime("00:00");
        c2STransferCommReqDTO.setAllowedToTime("10:00");

        List<DispHeaderColumn> dispHeaderColumnList = new ArrayList<>();
        DispHeaderColumn header = new DispHeaderColumn();
        header.setColumnName("transdateTime");
        header.setDisplayName("transdateTime");

        dispHeaderColumnList.add(header) ;

        c2STransferCommReqDTO.setDispHeaderColumnList(dispHeaderColumnList);
        C2STransferCommDownloadResp response = new C2STransferCommDownloadResp();
 HashMap<String, String> actualExecuteResult = this.c2STransferCommReportProcessor.execute(c2STransferCommReqDTO,
                response);


    }
}

