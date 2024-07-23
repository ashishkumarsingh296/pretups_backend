package com.inter.postvfe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @author rahul.dutt
 * 
 */
public class ReqRespServlet extends HttpServlet {
    public static Properties properties = new Properties();
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private HttpURLConnection _urlConnection = null;
    protected PrintWriter _out = null;
    protected BufferedReader _in = null;
    private String vfefilepath;

    public void init(ServletConfig conf) throws ServletException {
        if (_log.isDebugEnabled())
            _log.debug("init", "Entered");
        super.init(conf);
        // vfefilepath =
        // getServletContext().getRealPath(getInitParameter("postpaidpath"));
        if (_log.isDebugEnabled())
            _log.debug("init", "Exiting cs3cp6lResponseFilePath=" + vfefilepath);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void destroy() {
        super.destroy();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        StringBuffer lineBuff;
        String soapUrl = "http://10.230.85.75:8085/CMSWebService/CMSInvokeService";
        // soapUrl="http://172.16.1.121:5079/pretups/C2SReceiver?REQUEST_GATEWAY_CODE=USSD&REQUEST_GATEWAY_TYPE=USSD&LOGIN=pretups&PASSWORD=pretups123&SOURCE_TYPE=USSD&SERVICE_PORT=190";
        try {
            String message = "";
            lineBuff = new StringBuffer();
            String strReq = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            while ((strReq = bufferedReader.readLine()) != null)
                lineBuff.append(strReq);
            message = lineBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("ReqRespServlet", "message = " + message);
            System.out.println("REQ" + message);
            getUrlCOnnection(soapUrl, 1000, 1000, "Y", message.length(), "", "");
            try {
                _urlConnection.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            _out.flush();
            _out.println(message);
            _out.flush();
            setBufferedReader();
            StringBuffer buffer = new StringBuffer();
            String responsefrom = "";
            while ((responsefrom = _in.readLine()) != null) {
                buffer.append(responsefrom);
            }

            // File file = new File(vfefilepath);
            // properties.load(new FileInputStream(file));
            System.out.println("RESP:" + buffer.toString());
            out.print(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("doPost", "Exception e:" + e.getMessage());
        }// end of catch-Exception
        finally {
            try {
                close();
            } catch (Exception e) {
            }
            System.out.println(" Exiting ReqRespServlet:");
        }
    }// end of dePost

    void getUrlCOnnection(String p_url, int p_connectTimeout, int p_readTimeout, String p_keepAlive, long p_contentLength, String p_hostName, String p_userAgent) throws Exception {
        URL url = new URL(p_url);
        _urlConnection = (HttpURLConnection) url.openConnection();
        _urlConnection.setConnectTimeout(p_connectTimeout);
        _urlConnection.setReadTimeout(p_readTimeout);
        _urlConnection.setDoOutput(true);
        _urlConnection.setDoInput(true);
        _urlConnection.setRequestMethod("POST");
        setRequestHeader(p_contentLength, p_hostName, p_userAgent, p_keepAlive);
        setPrintWriter();
    }

    protected void setPrintWriter() throws Exception {
        try {
            _out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_urlConnection.getOutputStream())), true);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("setPrintWriter", "Exception " + e.getMessage());
            throw e;
        }
    }

    protected void setBufferedReader() throws Exception {
        try {
            _in = new BufferedReader(new InputStreamReader(_urlConnection.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("setBufferedReader", "Exception " + e.getMessage());
            throw e;
        }
    }

    private void setRequestHeader(long p_contenetLength, String p_host, String p_userAgent, String p_keepAlive) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setRequestHeader", "Entered p_contenetLength::" + p_contenetLength + " p_host::" + p_host + " p_userAgent::" + p_userAgent);
        try {
            _urlConnection.setRequestProperty("Host", p_host);
            _urlConnection.setRequestProperty("User-Agent", p_userAgent);
            _urlConnection.setRequestProperty("Content-Length", String.valueOf(p_contenetLength));
            _urlConnection.setRequestProperty("Content-Type", "text/xml");
            if ("Y".equalsIgnoreCase(p_keepAlive))
                _urlConnection.setRequestProperty("Connection", "keep-alive");
            else
                _urlConnection.setRequestProperty("Connection", "close");
        } catch (Exception e) {
            _log.error("setRequestHeader", "Exception e=" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setRequestHeader", "Exited");
        }

    }

    public void close() throws Exception {
        try {
            if (_out != null)
                _out.close();
        } catch (Exception e) {
        }
        try {
            if (_in != null)
                _in.close();
        } catch (Exception e) {
        }
        try {
            if (_urlConnection != null)
                _urlConnection.disconnect();
        } catch (Exception e) {
        }
    }
}
