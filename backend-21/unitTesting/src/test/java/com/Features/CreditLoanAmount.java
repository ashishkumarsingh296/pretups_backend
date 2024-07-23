package com.Features;

import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CAPI;
import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CDP;
import com.apicontrollers.extgw.o2ctransfer.EXTGW_O2CDAO;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.*;
import com.commons.*;
import com.dbrepository.DBHandler;
import com.jcraft.jsch.*;
import com.pageobjects.channeluserspages.c2ctransfer.C2CDetailsPage;
import com.pageobjects.channeluserspages.c2ctransfer.C2CTransferConfirmPage;
import com.pageobjects.channeluserspages.c2ctransfer.C2CTransferDetailsPage;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.sublinks.ChannelUserSubLinkPages;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.sshmanager.ConnectionManager;
import com.sshmanager.SSHService;
import com.utils.*;
import io.restassured.path.xml.XmlPath;
import org.openqa.selenium.WebDriver;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CreditLoanAmount extends BaseTest {

    public WebDriver driver;
    Map<String, String> ResultMap;
    String MasterSheetPath = _masterVO.getProperty("DataProvider");
    Login login;
    String selectedNetwork;
    RandomGeneration RandomGenerator;
    SelectNetworkPage networkPage;
    public static boolean TestCaseCounter = false;
    String executedDate;
    String currentDate;
    String processID;
    HashMap<String, String> c2cTransferMap=new HashMap<String, String>();
    HashMap<String, String> o2cTransferMap=new HashMap<String, String>();
    ChannelUserHomePage CHhomePage;
    C2CTransferDetailsPage C2CTransferDetailsPage;
    C2CDetailsPage C2CDetailsPage;
    C2CTransferConfirmPage C2CTransferConfirmPage;
    ChannelUserSubLinkPages chnlSubLink;

    public CreditLoanAmount(WebDriver driver) {
        this.driver = driver;
        ResultMap = new HashMap();
        login = new Login();
        RandomGenerator = new RandomGeneration();
        selectedNetwork = _masterVO.getMasterValue("Network Code");
        networkPage = new SelectNetworkPage(driver);
        C2CTransferDetailsPage = new C2CTransferDetailsPage(driver);
        C2CDetailsPage = new C2CDetailsPage(driver);
        C2CTransferConfirmPage = new C2CTransferConfirmPage(driver);
        CHhomePage = new ChannelUserHomePage(driver);
        chnlSubLink= new ChannelUserSubLinkPages(driver);
    }


    public void createLoanThresholdData(String MSISDN, String productCode, int loanAmount, long threshold, String categoryName) throws IOException {
        final String methodname = "createLoanThresholdData";
        Log.methodEntry(methodname, MSISDN, productCode, loanAmount,threshold);
        String path = _masterVO.getProperty("SOSFilePath");
        try {
            Log.info("Trying to write into CSV File.. ");
            FileWriter fw = new FileWriter(path);
            fw.write("Retailer MSISDN, Loan amount,Loan Threshold,Product Code \n");
            fw.write(MSISDN +"," + loanAmount +"," + threshold + "," + productCode);
            fw.close();
            Log.info("Written to CSV File : " + MSISDN + ", " + loanAmount + ", " + threshold + ", " + productCode);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void findPathFromConstant() {
        Log.methodEntry("findPathFromConstant");
        String constantProps = _masterVO.getMasterValue(MasterI.CONSTANT_PROPS);
        String source = _masterVO.getProperty("SOSFilePath");
        Session session;

        try {
            Log.info("Establishing connection with SSD server..");
            session = ConnectionManager.getInstance();
        } catch (JSchException ex) {
            Log.error("Error while getting SSH Server Instance : " + ex);
            return;
        }

        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            InputStream stream = sftpChannel.get(constantProps);
            Properties prop = new Properties();
            //load properties file
            try {
                Log.info("Trying to load Constant.props file..");
                prop.load(stream);
                Log.info("Constant.props file loaded.");
            } catch (IOException e) {
                Log.error("Error while loading Constant.props file");
                e.printStackTrace();
            }

            System.out.println(prop.get("USER_LOAN_FILE_PATH"));
            String Destination = prop.getProperty("USER_LOAN_FILE_PATH");

            Log.info("Path defined in property file : " +Destination);

            sftpChannel.put(source, Destination);
            sftpChannel.exit();
            channel.disconnect();

            Log.info("CSV file is placed to the path: " + Destination);

        } catch (JSchException JSchEx) {
            Log.error("Error while opening SFTP Channel : " + JSchEx);
        } catch (SftpException SftpEx) {
            Log.error("Error while fetching "  + " file from SSH Server : " + SftpEx);
        }
    }

    public void UserLoanDataUploadScriptExecution() {
        Log.methodEntry("UserLoanDataUploadScriptExecution");
        String ULDUScript = _masterVO.getProperty("USERLOANDATAUPLOAD");
        processID = "USRLOANLIST";
        Log.info("Trying to execute Script : " + ULDUScript);
        SSHService.executeScript(ULDUScript);
        executedDate = DBHandler.AccessHandler.getExecutedDate(processID);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        currentDate = dateFormat.format(date);
        System.out.println(currentDate);
        Assertion.assertContainsEquals(executedDate, currentDate);
        Log.info("Script executed successfully");
    }


    public HashMap<String, String> creditLoanAmountC2CT(String domainName, String categoryName, String MSISDN, int threshold, int loanAmount, String loginID, String productCode, int rowNum, String ToCategory, String PIN, String toMSISDN) throws InterruptedException , SQLException, ParseException {
        final String methodname = "addLoanProfileDaily";
        Log.methodEntry(methodname, domainName, categoryName);

        login.UserLogin(driver, "ChannelUser", categoryName);
        networkPage.selectNetwork();

        String balance = DBHandler.AccessHandler.getUserBalance(productCode,loginID);
        int usrBalance = (Integer.parseInt(balance))/100;

        int Qty = 1000;

        CHhomePage.clickC2CTransfer();
        chnlSubLink.clickC2CTransferLink();
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

        C2CTransferDetailsPage.enterMobileNo(toMSISDN);
        C2CTransferDetailsPage.clickSubmit();
        C2CDetailsPage.enterRefNum(RandomGenerator.randomNumeric(6));
        c2cTransferMap.put("InitiatedQuantities", C2CTransferDetailsPage.enterQuantityforC2C(Qty));
        C2CDetailsPage.enterRemarks("Remarks entered for C2C to: "+ToCategory);
        C2CDetailsPage.enterSmsPin(PIN);

        C2CDetailsPage.clickSubmit();
        C2CTransferConfirmPage.clickConfirm();
        String message = C2CDetailsPage.getMessage();

        /*EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();

        HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
        HashMap<String, String> initiatedQuantities = new HashMap<String, String>();
        initiatedQuantities.put(EXTGWC2CDP.ProductCode, apiData.get(C2CTransferAPI.QTY));

        apiData.put(C2CTransferAPI.LOGINID, "");
        apiData.put(C2CTransferAPI.PASSWORD, "");
        apiData.put(C2CTransferAPI.QTY, qty);
        String API = C2CTransferAPI.prepareAPI(apiData);

        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);

        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        *//*Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());*/

        String balanceWithLoan = DBHandler.AccessHandler.getUserBalance(productCode,loginID);
        int balanceWithC2C = (Integer.parseInt(balanceWithLoan))/100;
        Log.info("Balance before C2C Transfer : " + usrBalance);

        String balC2C=String.valueOf(balanceWithC2C);

        Log.info("Balance after C2C Transfer : " + balanceWithC2C);
        usrBalance = usrBalance - Qty + loanAmount;
        Log.info("Expected Balance : " + usrBalance);

        String usrBal=String.valueOf(usrBalance);

        c2cTransferMap.put("Actual Balance", balC2C);
        c2cTransferMap.put("Expected Balance", usrBal);

        String userID = DBHandler.AccessHandler.getUserIDFromMSISDN(MSISDN);

        String loanGiven = DBHandler.AccessHandler.getLoanGiven(userID);

        c2cTransferMap.put("Loan Given", loanGiven);

        Log.methodExit(methodname);
        return c2cTransferMap;
    }


    public HashMap<String, String> loanAmountSettlementO2C(String domainName, String categoryName, String MSISDN, int threshold, int loanAmount, String loginID, String productCode, int rowNum, String ToCategory, String PIN, String toMSISDN) throws InterruptedException , SQLException, ParseException {
        final String methodname = "loanAmountSettlementO2C";
        Log.methodEntry(methodname, domainName, categoryName);

        login.UserLogin(driver, "Operator", categoryName);
        networkPage.selectNetwork();

        String balance = DBHandler.AccessHandler.getUserBalance(productCode,loginID);
        int usrBalance = (Integer.parseInt(balance))/100;

        int Qty = 1000;
        String premium1 = _masterVO.getProperty("LPPCTRate2");

        int premium=Integer.parseInt(premium1);

        int O2CQty = 2000 + (2000 * premium/100);

        String O2CQty1 = String.valueOf(O2CQty);

        EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

        Object[] dataObject = EXTGWO2CDP.getAPIdataWithAllUsers();

        for (int i = 0; i < dataObject.length; i++) {
            EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();
            businessController businessController = new businessController(_masterVO.getProperty("O2CTransferCode"), null, apiData.get(O2CTransferAPI.MSISDN));

            apiData.put(O2CTransferAPI.EXTCODE, "");
            apiData.put(O2CTransferAPI.QTY,O2CQty1);
            TransactionVO TransactionVO = businessController.preparePreTransactionVO();

            String API = O2CTransferAPI.prepareAPI(apiData);
            String trimmedAPI = API.replaceAll("[\n\r]", "");
            System.out.println(trimmedAPI);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, trimmedAPI);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

        }

        String balanceWithLoan = DBHandler.AccessHandler.getUserBalance(productCode,loginID);
        int balanceWithO2C = (Integer.parseInt(balanceWithLoan))/100;
        Log.info("Balance before C2C Transfer : " + usrBalance);

        String balO2C=String.valueOf(balanceWithO2C);

        Log.info("Balance after C2C Transfer : " + balanceWithO2C);
        usrBalance = usrBalance + Qty - loanAmount;
        Log.info("Expected Balance : " + usrBalance);

        String usrBal=String.valueOf(usrBalance);


        o2cTransferMap.put("Actual Balance", balO2C);
        o2cTransferMap.put("Expected Balance", usrBal);

        String userID = DBHandler.AccessHandler.getUserIDFromMSISDN(MSISDN);

        String loanGiven = DBHandler.AccessHandler.getLoanGiven(userID);

        o2cTransferMap.put("Loan Given", loanGiven);

        Log.methodExit(methodname);
        return o2cTransferMap;
    }

}
