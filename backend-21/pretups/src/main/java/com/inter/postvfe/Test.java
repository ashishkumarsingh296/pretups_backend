package com.inter.postvfe;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;

import javax.xml.rpc.Stub;
import javax.xml.rpc.encoding.Serializer;

import com.btsl.pretups.inter.module.InterfaceUtil;
import com.inter.postvfe.postvfestub.CMSInvoke;
import com.inter.postvfe.postvfestub.CMSInvokeServiceLocator;
import com.inter.postvfe.postvfestub.CMSParamType;
import com.inter.postvfe.postvfestub.CMSRequest;
import com.inter.postvfe.postvfestub.CMSResponse;

/**
 * @author rahul.dutt
 * 
 */
class Test {
    public static void main(String[] args) {
        try {
            Test tt = new Test();

            // org.apache.log4j.PropertyConfigurator.configure("/pretupshome/tomcat5_web/webapps/pretups/WEB-INF/classes/configfiles/LogConfig.props");
            // org.apache.axis.i18n.ProjectResourceBundle.getBundle("pretups","com.btsl.pretups.inter.postvfe",Test.class.getName());
            if (args[0].equalsIgnoreCase("1"))
                tt.testOnLineView();
            else if (args[0].equalsIgnoreCase("2"))
                tt.testHotLine();
            else if (args[0].equalsIgnoreCase("3"))
                tt.testCustValidation();
            else if (args[0].equalsIgnoreCase("4"))
                tt.testCustValOnline();
            else if (args[0].equalsIgnoreCase("5"))
                tt.testCustRCOnline();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            // try{}catch()
        }
    }

    private void testCustValOnline() {
        System.out.println("==========testCustValOnline====================");
        CMSRequest cmsRequest = null;
        CMSResponse cmsResponse = null;
        Stub _stubSuper = null;
        CMSInvoke clientStub = null;
        String url = "http://172.16.1.121:5079/pretups/C2SReceiver?REQUEST_GATEWAY_CODE=USSD&REQUEST_GATEWAY_TYPE=USSD&LOGIN=pretups&PASSWORD=pretups123&SOURCE_TYPE=USSD&SERVICE_PORT=190";
        url = "http://172.16.1.121:5079/pretups/ReqRespServlet";
        url = "http://10.230.85.55:8085/CMSWebService/CMSInvokeService";
        try {
            // EngineConfiguration config = new
            // FileProvider("client-config.wsdd");
            CMSInvokeServiceLocator test = new CMSInvokeServiceLocator();
            clientStub = test.getCMSInvoke();
            _stubSuper = (Stub) clientStub;
            _stubSuper._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, url);
            // _stubSuper._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY,"http://172.16.1.121:5069/pretups/C2SSubscriberReceiver");
            cmsRequest = new CMSRequest();
            cmsRequest.setUsername("ETOPUP");
            cmsRequest.setPassword("SY");
            cmsRequest.setWfname("PG_ValidateInquiry");
            // ----------------------
            // ----------------------
            CMSParamType[] cmsParam = new CMSParamType[2];
            cmsParam[0] = new CMSParamType("CUSTOMER_ID", "", "1005226888", null);
            cmsParam[1] = new CMSParamType("MSISDN", "", "1005226888", null);
            cmsRequest.setParams(cmsParam);
            System.out.println("==REQUEST==" + cmsRequest.toString() + "URL:" + _stubSuper._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY));
            System.out.println("Wfname" + cmsRequest.getWfname() + "config:");
            for (int i = 0; i < cmsParam.length; i++) {
                System.out.println("REQ: name:" + cmsParam[i].getName() + "value:" + cmsParam[i].getSimpleValue());
            }
            // QName portName = new QName( url,"CMSInvoke_PortType");
            // LogHandler loghand=new LogHandler();
            // HandlerRegistry registry = test.getHandlerRegistry();
            // List handlerList = new ArrayList();
            // handlerList.add( new HandlerInfo(LogHandler.class, null, null )
            // );
            // registry.setHandlerChain( portName, handlerList );

            if (clientStub == null) {
                throw new Exception("Client Initiate Error");
            }
            cmsResponse = clientStub.invoke(cmsRequest);

            String resultCode = cmsResponse.getStatusCode();
            String resultDesc = cmsResponse.getStatusDesc();
            System.out.println(new java.util.Date() + ":resultCode:" + resultCode + ":resultDesc:" + resultDesc);
            CMSParamType[] outparams = cmsResponse.getOutparams();
            if (outparams != null)
                for (int i = 0; i < outparams.length; i++) {
                    System.out.println("====[" + i + "]RESP:" + "O/P name=" + outparams[i].getName() + ":" + "value=" + outparams[i].getSimpleValue());
                    com.inter.postvfe.postvfestub.CMSListType[] listValues = outparams[i].getListValues();
                    if (listValues != null)
                        for (int j = 0; j < listValues.length; j++) {
                            com.inter.postvfe.postvfestub.CMSParamType[] paramList = listValues[j].getParamList();
                            for (int k = 0; k < paramList.length; k++) {
                                System.out.println("#####[" + k + "]RESP:" + "O/P name=" + paramList[k].getName() + ":" + "value=" + paramList[k].getSimpleValue());
                            }
                        }
                }
        } catch (org.apache.axis.AxisFault af) {
            System.out.println("AxisFault" + af);
            System.out.println("Fault Detail" + af.detail);
            af.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(" Exiting :testCustValOnline");
        }
    }

    private void testCustRCOnline() {
        System.out.println("==========testCustRCOnline====================");
        CMSInvoke clientStub = null;
        CMSRequest cmsRequest = null;
        CMSResponse cmsResponse = null;
        Stub _stubSuper = null;
        String url = "http://172.16.1.121:5079/pretups/C2SReceiver?REQUEST_GATEWAY_CODE=USSD&REQUEST_GATEWAY_TYPE=USSD&LOGIN=pretups&PASSWORD=pretups123&SOURCE_TYPE=USSD&SERVICE_PORT=190";
        url = "http://172.16.1.121:5079/pretups/ReqRespServlet";
        url = "http://10.230.85.55:8085/CMSWebService/CMSInvokeService";
        try {
            CMSInvokeServiceLocator test = new CMSInvokeServiceLocator();
            clientStub = test.getCMSInvoke();
            _stubSuper = (Stub) clientStub;
            _stubSuper._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, url);
            if (clientStub == null) {
                throw new Exception("Client Initiate Error");
            }
            cmsRequest = new CMSRequest();
            cmsRequest.setUsername("ETOPUP");
            cmsRequest.setPassword("SY");
            cmsRequest.setWfname("WritePayment");
            CMSParamType[] cmsParam = new CMSParamType[9];
            cmsParam[0] = new CMSParamType("SYNCHRONOUS_MODE", "", "true", null);
            cmsParam[1] = new CMSParamType("TRANSX_CODE", "", "CE2DD-X3", null);
            cmsParam[2] = new CMSParamType("RT_CACHKNUM", "", "TEST1234", null);
            cmsParam[3] = new CMSParamType("RT_CACHKAMT_PAY", "", "100.10", null);
            cmsParam[4] = new CMSParamType("RT_CARECDATE", "", InterfaceUtil.getCurrentDateString("yyyy-MM-dd HH:mm"), null);
            cmsParam[5] = new CMSParamType("RT_CACHKDATE", "", InterfaceUtil.getCurrentDateString("yyyy-MM-dd HH:mm"), null);
            cmsParam[6] = new CMSParamType("CS_ID", "", "62542699", null);
            cmsParam[7] = new CMSParamType("PAYMENT_MODE", "", "C", null);
            cmsParam[8] = new CMSParamType("PAYMENT_CURRENCY_ID", "", "43", null);
            cmsRequest.setParams(cmsParam);
            System.out.println("==REQUEST==" + cmsRequest);
            for (int i = 0; i < cmsParam.length; i++) {
                System.out.println("REQ: name:" + cmsParam[i].getName() + "value:" + cmsParam[i].getSimpleValue());
            }
            System.out.println("");
            cmsResponse = clientStub.invoke(cmsRequest);
            String resultCode = cmsResponse.getStatusCode();
            String resultDesc = cmsResponse.getStatusDesc();
            System.out.println(new java.util.Date() + ":resultCode:" + resultCode + ":resultDesc:" + resultDesc);
            CMSParamType[] outparams = cmsResponse.getOutparams();
            if (outparams != null)
                for (int i = 0; i < outparams.length; i++) {
                    System.out.println("Output:" + "O/P name=" + outparams[i].getName() + ":" + "value=" + outparams[i].getSimpleValue());
                }
            System.out.println("testCustRCOnline");
            testCustValOnline();
        } catch (org.apache.axis.AxisFault af) {
            System.out.println("AxisFault" + af);
            System.out.println("Fault Detail" + af.detail);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(" Exiting :testCustRCOnline");
        }
    }

    private void testCustValidation() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // Connection con =
            // DriverManager.getConnection("jdbc:oracle:thin:@172.30.37.34:1521:prtp",
            // "pretups_test", "pretups_test");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@10.230.85.55:1524:BSCSTST1", "GMD", "GMD");

            // String query = "call VF_READ_TOPUP_ELIGIBLITY(?,?,?,?,?,?,?,?)";
            String query = "call sysadm.VF_READ_TOPUP_ELIGIBLITY(?,?,?,?,?,?,?,?)";
            System.out.println("1");
            CallableStatement stmt = con.prepareCall(query);
            System.out.println("2");
            stmt.setString(1, "1005226888");
            stmt.setString(2, "P100215");
            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.registerOutParameter(4, Types.VARCHAR);
            stmt.registerOutParameter(5, Types.DOUBLE);
            stmt.registerOutParameter(6, Types.INTEGER);
            stmt.registerOutParameter(7, Types.INTEGER);
            stmt.registerOutParameter(8, Types.VARCHAR);

            System.out.println("3");
            // execute and retrieve the result set
            stmt.execute();
            System.out.println("1061436664");

            System.out.println("accountNumber: " + stmt.getInt(3) + ",accountStatus: " + stmt.getString(4) + " ,balance: " + stmt.getDouble(5) + " ,languageId: " + stmt.getInt(6) + " ,errorCode: " + stmt.getInt(7) + " ,errorDesc: " + stmt.getString(8));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            // e.printStackTrace();
        } finally {
            // try{}catch()
        }
    }

    private void testHotLine() {
    }

    private void testOnLineView() {
        try {
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // e.printStackTrace();
        } finally {
            // try{}catch()
        }
    }
}
