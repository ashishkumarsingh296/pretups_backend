package com.testscripts.sit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2SCardGroup;
import com.Features.C2STransferSpring;
import com.Features.ChannelUser;
import com.Features.CommissionProfile;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.Features.TransferControlProfile;
import com.Features.mapclasses.C2STransferSpringMap;
import com.Features.mapclasses.ChannelUserMap;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.dbrepository.DBHandler;
import com.utils.ExtentI;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_C2STransferSpring extends BaseTest {
                static boolean TestCaseCounter = false;

                Map<String,String> dataMap;
                ChannelUserMap chnlUsrMap;
                HashMap<String, String> paraMap;
                C2STransferSpringMap c2STransferSpringMap;
                ChannelUser chnlUsr;
                RandomGeneration randstr;
                GenerateMSISDN gnMsisdn;
                SuspendChannelUser suspendCHNLUser;
                ResumeChannelUser resumeCHNLUser;              
                TransferControlProfile trfCntrlProf;
                C2SCardGroup c2sCardGroup ;

                @BeforeClass
                public void dataC2S(){
                                chnlUsrMap = new ChannelUserMap();
                                paraMap = (HashMap<String, String>) chnlUsrMap.getChannelUserMap(null, null);
                                c2STransferSpringMap = new C2STransferSpringMap();
                                chnlUsr = new ChannelUser(driver);
                                randstr = new RandomGeneration();
                                gnMsisdn = new GenerateMSISDN();
                                suspendCHNLUser = new SuspendChannelUser(driver);
                                resumeCHNLUser = new ResumeChannelUser(driver);
                                trfCntrlProf = new TransferControlProfile(driver);
                                c2sCardGroup= new C2SCardGroup(driver);
                }

                @DataProvider(name = "testData")
                public Object[][] testData() {


                                String[] description=new String[16];
                                description[0]="To verify that user is unable to recharge when  pin  is null";
                                description[1]="To verify that user is unable to recharge when  pin  is non numeric";
                                description[2]="To verify that user is unable to recharge when  amount  is null";
                                description[3]="To verify that user is unable to recharge when  amount  is negative";
                                description[4]="To verify that user is unable to recharge when  subscriber msisdn is null";
                                description[5]="To verify that user is unable to recharge when  subscriber msisdn is non numeric";
                               // description[6]="To verify that user is unable to recharge when  pin entered is invalid";
                                description[7]="To verify that user is able to recharge when all fields are valid";
                                description[8]="To verify that user is unable to recharge when  user is out suspended";
                                description[9]="To verify that user is unable to recharge when  receiver msisdn entered is invalid";
                                description[10]="To verify that user is unable to recharge when  user is suspended";
                                //description[11]="To verify that user is unable to recharge when  commision profile is suspended";
                               // description[12]="To verify that user is unable to recharge when  card group is suspended";
                                description[13]="To verify that Channel user should not be able to perform C2S recharge if associated TCP is suspended in the system.";
                               /* description[14]="To verify that channel user is not able to perform C2S recharge if Recharge amount is less than the Minimum amount defined in  'Per C2S transaction amount' in the associated TCP.";
                                description[15]="To verify that channel user is not able to perform C2S recharge if Recharge amount is greater than the maximum amount defined in  'Per C2S transaction amount' in the associated TCP.";*/



                                Object[][] testData = {{0, description[0], c2STransferSpringMap.setC2SMap("pin", "")},
                                                                                {1, description[1], c2STransferSpringMap.setC2SMap("pin", "abcd")},
                                                                                {2, description[2], c2STransferSpringMap.setC2SMap("amount", "")},
                                                                                {3, description[3], c2STransferSpringMap.setC2SMap("amount", "-85")},
                                                                                {4, description[4], c2STransferSpringMap.setC2SMap("msisdn", "")},
                                                                                {5, description[5], c2STransferSpringMap.setC2SMap("msisdn", "8fgf**s")},
                                                                               //not handled {6, description[6], c2STransferSpringMap.setC2SMap("pin", "3333")},
                                                                                {7, description[7], c2STransferSpringMap.getC2SMap()},
                                                                {8, description[8], c2STransferSpringMap.getC2SMap()},
                                                                {9, description[9], c2STransferSpringMap.getC2SMap()},
                                                                {10, description[10], c2STransferSpringMap.getC2SMap()},
                                                               //not handled  {11, description[11], c2STransferSpringMap.getC2SMap()},
                                                              //not handled {12, description[12], c2STransferSpringMap.getC2SMap()},
                                                                {13, description[13], c2STransferSpringMap.getC2SMap()},
                                                              //not handled {14, description[14], c2STransferSpringMap.getC2SMap()},
                                                              //not handled  {15, description[15], c2STransferSpringMap.getC2SMap()}
                                };
                                return testData;
                }


                @Test(dataProvider = "testData")
                public void c2sTransferSIT(int caseNum,String description, HashMap<String, String> mapParam) throws IOException, InterruptedException{
                                Log.startTestCase(this.getClass().getName());

                                if (TestCaseCounter == false) { 
                                                test = extent.createTest("[SIT]C2S Transfer");
                                                TestCaseCounter = true;
                                }
                                C2STransferSpring c2sTransferSpring = new C2STransferSpring(driver);
                                Map<String, String> resultMap = null;
                                String actualMsg,expectedMsg;
                                currentNode=test.createNode(description);
                                currentNode.assignCategory("SIT");
                                switch(caseNum){
                                case 0:
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,false,false);
                                                actualMsg = resultMap.get("fieldError");
                                                expectedMsg = (MessagesDAO.prepareMessageByKey("pretups.recharge.pin.is.required"));
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                break;

                                case 1:
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,false,false);
                                                actualMsg =  resultMap.get("fieldError");
                                                expectedMsg = MessagesDAO.prepareMessageByKey("pretups.recharge.pin.is.not.valid");
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                break;

                                case 2:
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,true,false);
                                                actualMsg =  resultMap.get("fieldError");
                                                expectedMsg = MessagesDAO.prepareMessageByKey("pretups.recharge.amount.is.required");
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                break;

                                case 3:
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,false,false);
                                                actualMsg =  resultMap.get("fieldError");
                                                expectedMsg = MessagesDAO.prepareMessageByKey("pretups.recharge.amount.is.greathan.or.equal.to.zero");
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                break;


                                case 4:
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,false,false);
                                                actualMsg =  resultMap.get("fieldError");
                                                expectedMsg = MessagesDAO.prepareMessageByKey("pretups.recharge.subscriberMsisdn.is.required");
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                break;


                                case 5:
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,false,false);
                                                actualMsg =  resultMap.get("fieldError");
                                                expectedMsg = MessagesDAO.prepareMessageByKey("pretups.recharge.msisdn.is.not.valid");
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                break;

                                case 6:
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,false,false);
                                                actualMsg =  resultMap.get("fieldError");
                                                expectedMsg = MessagesDAO.prepareMessageByKey("pretups.recharge.pin.is.not.valid");
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                break;   

                                case 7:
                                				
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,false,false);
                                                actualMsg =  resultMap.get("successMessage");
                                                String[] param={resultMap.get("receiverMSISDN"),resultMap.get("transferID"),resultMap.get("transferAmount"),resultMap.get("senderMSISDN"),resultMap.get("balance")};
                                                expectedMsg ="209:Transaction number "+param[1]+" to recharge "+ param[2] +" INR to "+ param[0]+" is UP. New Bal is "+param[4]+" INR.";
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                break;

                                case 8:
                                                paraMap.put("outSuspend_chk", "Y");  paraMap.put("searchMSISDN", c2STransferSpringMap.getC2SMap("fromMSISDN")); paraMap.put("loginChange", "N");
                                                paraMap.put("assgnPhoneNumber", "N");
                                                chnlUsr.modifyChannelUserDetails(c2STransferSpringMap.getC2SMap("category"), paraMap);
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,false,false);
                                                actualMsg =  resultMap.get("formError");
                                                expectedMsg = "7033:Sorry, you are not allowed for credit recharge.";
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                paraMap.put("outSuspend_chk", "N");
                                                ExtentI.Markup(ExtentColor.TEAL, "Removing OutSuspended status from Channel User");
                                                chnlUsr.modifyChannelUserDetails(c2STransferSpringMap.getC2SMap("category"), paraMap);
                                                break;

                                case 9:
                                                String prefix = new UniqueChecker().UC_PrefixData();
                                                String subsmsisdn = prefix + randstr.randomNumeric(gnMsisdn.generateMSISDN());
                                                mapParam.put("msisdn",subsmsisdn);
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,false,true);
                                                actualMsg =  resultMap.get("formError");
                                                expectedMsg = MessagesDAO.prepareMessageByKey("c2stranfer.c2srecharge.error.nonetworkprefix");
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                break;

                                case 10:
                                                suspendCHNLUser.suspendChannelUser_MSISDN(c2STransferSpringMap.getC2SMap("fromMSISDN"), "Automation Remarks");
                                                suspendCHNLUser.approveCSuspendRequest_MSISDN(c2STransferSpringMap.getC2SMap("fromMSISDN"), "Automation remarks");
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,false,false);
                                                actualMsg =  resultMap.get("loginError");
                                                expectedMsg ="User is not allowed to Login.";
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                resumeCHNLUser.resumeChannelUser_MSISDN(c2STransferSpringMap.getC2SMap("fromMSISDN"), "Auto Resume Remarks");
                                                break;

                                case 11:
                                                //no such scenario written before
                                                //on hold for now
                                                CommissionProfile commissionProfile = new CommissionProfile(driver);
                                                //commissionProfile.suspendAdditionalCommProfile(c2STransferSpringMap.getC2SMap("fromDomain"), c2STransferSpringMap.getC2SMap("category"), c2STransferSpringMap.getC2SMap("fromGrade"), c2STransferSpringMap.getC2SMap("fromCommProfile"));
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,false,false);
                                                actualMsg =  resultMap.get("formError");
                                                expectedMsg="Commission profile is suspended please try again later";
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                break;

                                case 12:
                                				//on hold
                                                c2sCardGroup.c2SCardGroupSuspend(c2STransferSpringMap.getC2SMap("cardGroupName"));
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,false,false);
                                                actualMsg =  resultMap.get("formError");
                                                expectedMsg="The Commission Profile" +c2STransferSpringMap.getC2SMap("cardGroupName")+ "is now suspended";
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                c2sCardGroup.c2SCardGroupActivateCardGroup(c2STransferSpringMap.getC2SMap("cardGroupName"));
                                                break;


                                case 13:
                                                trfCntrlProf.channelLevelTransferControlProfileSuspend(0,c2STransferSpringMap.getC2SMap("fromDomain"), c2STransferSpringMap.getC2SMap("category"),c2STransferSpringMap.getC2SMap("fromTCPName"), c2STransferSpringMap.getC2SMap("fromTCPID"));
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,false,false);
                                                actualMsg =  resultMap.get("formError");
                                                expectedMsg = "7041:This service is not available with your current profile.";
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                trfCntrlProf.channelLevelTransferControlProfileActive(0,c2STransferSpringMap.getC2SMap("fromDomain"), c2STransferSpringMap.getC2SMap("category"),c2STransferSpringMap.getC2SMap("fromTCPName"), c2STransferSpringMap.getC2SMap("fromTCPID"));
                                                break;

                                case 14:
                                                Object[][] data1= DBHandler.AccessHandler.getProductDetailsForC2S(c2STransferSpringMap.getC2SMap("fromLoginID"), _masterVO.getProperty("CustomerRechargeCode"));
                                                String productName = data1[0][5].toString();
                                                trfCntrlProf.modifyTCPPerC2SminimumAmt(c2STransferSpringMap.getC2SMap("fromDomain"), c2STransferSpringMap.getC2SMap("category"), c2STransferSpringMap.getC2SMap("fromTCPID"), "100", "100", productName);
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,false,false);
                                                Object[][] tcp1= DBHandler.AccessHandler.getTransferProfileDetails();
                                                String MinAmt = tcp1[0][3].toString();
                                                String MaxAmt = tcp1[0][5].toString();
                                                actualMsg =  resultMap.get("formError");
                                               // String[]  strArr =  { resultMap.get("transferAmount"),MinAmt ,MaxAmt };
                                                expectedMsg = "6019:Requested amount"+ resultMap.get("transferAmount") + "should be in the range of "+MinAmt+ "and"+ MaxAmt;
                                                Log.info(actualMsg+" |GAP| "+ expectedMsg);
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                trfCntrlProf.modifyTCPPerC2SminimumAmt(c2STransferSpringMap.getC2SMap("fromDomain"), c2STransferSpringMap.getC2SMap("category"), c2STransferSpringMap.getC2SMap("fromTCPID"), _masterVO.getProperty("MinimumBalance"),_masterVO.getProperty("AllowedMaxPercentage"), productName);
                                                break;
                                                
                                case 15:
                                                Object[][] data2= DBHandler.AccessHandler.getProductDetailsForC2S(c2STransferSpringMap.getC2SMap("fromLoginID"), _masterVO.getProperty("CustomerRechargeCode"));
                                                String productName2 = data2[0][5].toString();
                                                trfCntrlProf.modifyTCPPerC2SmaximumAmt(c2STransferSpringMap.getC2SMap("fromDomain"), c2STransferSpringMap.getC2SMap("fromCategory"), c2STransferSpringMap.getC2SMap("fromTCPID"), "100", "100", productName2);
                                                resultMap = c2sTransferSpring.performC2STransfer(mapParam,false,false);
                                                Object[][] tcp2= DBHandler.AccessHandler.getTransferProfileDetails();
                                                String MinAmt2 = tcp2[0][3].toString();
                                                String MaxAmt2 = tcp2[0][5].toString();
                                                actualMsg =  resultMap.get("formError");
                                                expectedMsg = "6019:Requested amount"+ resultMap.get("transferAmount") + "should be in the range of "+MinAmt2+ "and"+ MaxAmt2;
                                                Log.info(actualMsg+" |GAP| "+ expectedMsg);
                                                Validator.messageCompare(actualMsg, expectedMsg);
                                                trfCntrlProf.modifyTCPPerC2SmaximumAmt(c2STransferSpringMap.getC2SMap("fromDomain"), c2STransferSpringMap.getC2SMap("fromCategory"), c2STransferSpringMap.getC2SMap("fromTCPID"), _masterVO.getProperty("MaximumBalance"),_masterVO.getProperty("AllowedMaxPercentage"), productName2);                    

                                                break;
                                }
                }
}
