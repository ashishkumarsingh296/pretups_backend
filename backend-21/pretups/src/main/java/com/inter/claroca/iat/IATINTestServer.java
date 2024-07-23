/*
 * Created on Jul 16, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroca.iat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import com.btsl.pretups.iat.transfer.businesslogic.IATInterfaceVO;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.util.ConfigServlet;

public class IATINTestServer {
    public static Properties properties = new Properties();

    static IATInterfaceVO p_iatReqResVO = new IATInterfaceVO();

    public static void load(String fileName) throws IOException {
        System.out.println("File Path ::" + fileName);
        File file = new File(fileName);
        properties.load(new FileInputStream(file));
    }// end of load

    public static void main(String args[]) {
        String filePath = null;
        String action = null;

        try {
            if (args.length == 4) {

                File constantsFile = new File(args[0]);
                if (!constantsFile.exists()) {
                    System.out.println(" Constants File Not Found .............");
                    return;
                }
                File logconfigFile = new File(args[1]);
                if (!logconfigFile.exists()) {
                    System.out.println(" Logconfig File Not Found .............");
                    return;
                }
                ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
                FileCache.loadAtStartUp();

                filePath = args[2];
                action = args[3];

                System.out.println("File Path ::" + filePath);
                System.out.println("Action ::" + action);

                load(filePath);
                new IATINTestServer().setIATVO(p_iatReqResVO);

                IATINHandler handleObj = new IATINHandler();

                if ("CREDIT".equalsIgnoreCase(action))
                    handleObj.credit(p_iatReqResVO);
                else if ("CHKSTATUS".equalsIgnoreCase(action))
                    handleObj.checkTxnStatus(p_iatReqResVO, "WEB", 1, 0);
            } else {
                System.out.println("Usage : IATINTestServer [Constants file] [LogConfig file] [IATConfig File] [CREDIT/CHKSTATUS]");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception thrown in IATINTestServer: Not able to load files" + e);
            ConfigServlet.destroyProcessCache();
            return;
        }
    }

    public void setIATVO(IATInterfaceVO p_iatReqResVO) {
        p_iatReqResVO.setIatAction((String) properties.get("_iatAction"));
        p_iatReqResVO.setIatServiceType((String) properties.get("_iatServiceType"));
        p_iatReqResVO.setIatReceiverMSISDN((String) properties.get("_iatReceiverMSISDN"));
        p_iatReqResVO.setIatReceiverCountryCode(Integer.parseInt((String) properties.get("_iatReceiverCountryCode")));
        p_iatReqResVO.setIatReceiverCountryShortName((String) properties.get("_iatReceiverCountryShortName"));
        p_iatReqResVO.setIatInterfaceId((String) properties.get("_iatInterfaceId"));
        p_iatReqResVO.setIatSenderNWTRXID((String) properties.get("_iatSenderNWTRXID"));
        p_iatReqResVO.setIatTRXID((String) properties.get("_iatTRXID"));
        p_iatReqResVO.setIatSenderNWID((String) properties.get("_iatSenderNWID"));
        p_iatReqResVO.setIatRcvrNWID((String) properties.get("_iatRcvrNWID"));
        p_iatReqResVO.setIatSenderNWTYPE((String) properties.get("_iatSenderNWTYPE"));
        p_iatReqResVO.setIatSenderCountryCode(Integer.parseInt((String) properties.get("_iatSenderCountryCode")));
        p_iatReqResVO.setIatType(Integer.parseInt((String) properties.get("_iatType")));
        p_iatReqResVO.setIatRetailerMsisdn((String) properties.get("_iatRetailerMsisdn"));
        p_iatReqResVO.setIatRetailerID((String) properties.get("_iatRetailerID"));
        p_iatReqResVO.setIatDeviceID((String) properties.get("_iatDeviceID"));
        p_iatReqResVO.setOption1((String) properties.get("_option1"));
        p_iatReqResVO.setOption2((String) properties.get("_option2"));
        p_iatReqResVO.setOption3((String) properties.get("_option3"));
        p_iatReqResVO.setIatModule((String) properties.get("_iatModule"));
        p_iatReqResVO.setIatUserType((String) properties.get("_iatUserType"));
        p_iatReqResVO.setIatInterfaceAmt(Long.parseLong((String) properties.get("_iatInterfaceAmt")));
        p_iatReqResVO.setIatRequestedAmount(Long.parseLong((String) properties.get("_iatRequestedAmount")));
        p_iatReqResVO.setIatSourceType((String) properties.get("_iatSourceType"));
        p_iatReqResVO.setIatINAccessType((String) properties.get("_iatINAccessType"));
        p_iatReqResVO.setIatNotifyMSISDN((String) properties.get("_iatNotifyMSISDN"));
        Date _currentDate = new Date();
        p_iatReqResVO.setIatSendingNWTimestamp(_currentDate);

        System.out.println("Request Parameters ::" + p_iatReqResVO.toString());
    }
}