package com.btsl.pretups.channel.receiver;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class ExtranetReceiver extends HttpServlet {
    
    private static final Log _log = LogFactory
			.getLog(ExtranetReceiver.class.getName());

    private String extraNetPretupsId = null;
    private String extraNetURL = null;

    public void init() throws ServletException {
        super.init();
        extraNetPretupsId = Constants.getProperty("EXTRANET_PRETUPS_ID");
        extraNetURL = Constants.getProperty("EXTRANET_URL");
        if (BTSLUtil.isNullString(extraNetPretupsId) || BTSLUtil.isNullString(extraNetPretupsId)) {
            _log.error("init", "EXTRANET_PRETUPS_ID or EXTRANET_URL not defined in Constant props");
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ExtranetReceiver[init]","","","","EXTRANET_PRETUPS_ID or EXTRANET_URL not defined in Constant props");
            // throw new
            // ServletException("EXTRANET_PRETUPS_ID or EXTRANET_URL not defined in Constant props");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String METHOD_NAME = "doPost";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug("doPost", "Entered");
        }
        HttpURLConnection con = null;
        String extranetSessionId = null;
        String extranetUserId = null;
        String extranetlangId = null;
        try {
            extranetSessionId = request.getParameter("VFESID");
            if (BTSLUtil.isNullString(extranetSessionId)) {
                extranetSessionId = request.getHeader("VFESID");
            }

            extranetUserId = request.getParameter("VFEUID");
            if (BTSLUtil.isNullString(extranetUserId)) {
                extranetUserId = request.getHeader("VFEUID");
            }

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("extranetSessionId: " );
            	loggerValue.append(extranetSessionId);
            	loggerValue.append(",, extranetUserId: ");
            	loggerValue.append(extranetUserId);

                _log.debug("doPost", loggerValue);
            }
            if (BTSLUtil.isNullString(extranetSessionId) && BTSLUtil.isNullString(extranetUserId)) {
                _log.error("doPost", "VFESID not received from Extranet.");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtranetReceiver[doPost]", "", "", "",
                    "VFESID or VFESID not received from Extranet");
                throw new BTSLBaseException("VFESID not received from Extranet");
            }
            if (!BTSLUtil.isNullString(extranetSessionId) && BTSLUtil.isNullString(extranetUserId)) {
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("INSIDE 11111");
                	loggerValue.append(extraNetURL);
                    _log.debug("doPost", loggerValue);
                }

                // URL url=new URL(extraNetURL);
                final URL url = new URL(extraNetURL + "VFESID=" + extranetSessionId + "&VFESYSID=" + extraNetPretupsId);
                con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(100000);
                con.setConnectTimeout(100000);
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "text/xml");
                // con.addRequestProperty("VFESID",extranetSessionId);
                // con.addRequestProperty("VFESYSID",extraNetPretupsId);
                con.setUseCaches(false);
                con.connect();
                try( final BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF8"));)
                {
                // Send data
                wr.write("");
                wr.flush();
                }
                // Get response
                final InputStream rd = con.getInputStream();
                int c = 0;
                String line = "";

                while ((c = rd.read()) != -1) {
                    // Process line...
                    // line += (char) c;
                    line += String.valueOf(Character.toChars(c));
                }

               
                rd.close();

                final HashMap map = BTSLUtil.getStringToHash(line, "&", "=");

                extranetlangId = (String) map.get("VFELANGID");
                if ("ar".equalsIgnoreCase(extranetlangId)) {
                    extranetlangId = "1";
                } else if ("eng".equalsIgnoreCase(extranetlangId)) {
                    extranetlangId = "0";
                } else {
                    extranetlangId = "1";
                }

                extranetUserId = (String) map.get("VFEUID");

                request.getSession(false).setAttribute("vfeUser", extranetUserId);
                request.getSession(false).setAttribute("vfeLang", extranetlangId);

                // ServletOutputStream out = response.getOutputStream();

                /*
                 * response.setContentType("text/html");
                 * PrintWriter out = response.getWriter();
                 * out.println("<script language=\"javascript\">");
                 * out.println("window.location.href="+
                 * "/pretups/login.do?method=loadUserDetails&vfeUser="
                 * +extranetUserId+"&vfeLang="+extranetlangId);
                 * out.println("</script>");
                 */

                /*
                 * response.setContentType("text/html");
                 * PrintWriter out = response.getWriter();
                 * out.println("<HTML><HEAD>");
                 * out.println("<script language=\"JavaScript\">");
                 * //out.println("window.location.href="+
                 * "/pretups/login.do?method=loadUserDetails&vfeUser="
                 * +extranetUserId+"&vfeLang="+extranetlangId);
                 * // out.println(
                 * "location.href=(\"http://172.30.37.34:5555/pretups/login.do?method=loadUserDetails&vfeUser="
                 * +extranetUserId+"&vfeLang="+extranetlangId+"\")");
                 * out.println(
                 * "location.href=(\"/pretups/login.do?method=loadUserDetails&vfeUser="
                 * +extranetUserId+"&vfeLang="+extranetlangId+"\")");
                 * 
                 * out.println("</script>");
                 * out.println("</HEAD></HTML>");
                 */

                response.sendRedirect("/pretups/login.do?method=loadUserDetails&vfeUser=" + extranetUserId + "&vfeLang=" + extranetlangId);

                // response.sendRedirect(response.encodeRedirectUrl("http://172.30.37.34:5555/pretups"+"/login.do?method=loadUserDetails&vfeUser=manisha&vfeLang=1"));
                // response.sendRedirect(response.encodeRedirectUrl("/pretups"+"/login.do?method=loadUserDetails&vfeUser=manisha&vfeLang=1"));
                if (_log.isDebugEnabled()) {
                    _log.debug("doPost", " Request Redirected..........");
                }

                // request.getSession(false).setAttribute("vfeUser",extranetUserId);
                // request.getSession(false).setAttribute("vfeLang",extranetlangId);
                // request.getRequestDispatcher("/login.do?method=loadUserDetails").forward(request,response);

            }
            // else if((BTSLUtil.isNullString(extranetSessionId) &&
            // !BTSLUtil.isNullString(extranetUserId)))
            else if ((!BTSLUtil.isNullString(extranetSessionId)) && (!BTSLUtil.isNullString(extranetUserId))) {
                if (_log.isDebugEnabled()) {
                    _log.debug("doPost", "INSIDE 22222");
                }
                extranetlangId = request.getParameter("VFELANGID");
                if (BTSLUtil.isNullString(extranetlangId)) {
                    extranetlangId = request.getHeader("VFELANGID");
                }
                if (BTSLUtil.isNullString(extranetlangId)) {
                    extranetlangId = "1";
                }
                request.getSession(false).setAttribute("vfeUser", extranetUserId);
                request.getSession(false).setAttribute("vfeLang", extranetlangId);
                request.getRequestDispatcher("/login.do?method=loadUserDetails").forward(request, response);

            }
            
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception " );
            loggerValue.append(e.getMessage());
            _log.error("doPost", loggerValue );
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("doPost", "Exited");
            }
            if (con != null) {
                con.disconnect();
            }
        }
    }

    /*
     * public void doPost(HttpServletRequest request, HttpServletResponse
     * response) throws ServletException, IOException
     * {
     * if(_log.isDebugEnabled())_log.debug("doPost","Entered");
     * HttpURLConnection con = null;
     * String extranetSessionId=null;
     * String extranetUserId=null;
     * String extranetlangId=null;
     * try
     * {
     * extranetSessionId=request.getParameter("VFESID");
     * if(BTSLUtil.isNullString(extranetSessionId))
     * extranetSessionId=request.getHeader("VFESID");
     * 
     * extranetUserId=request.getParameter("VFEUID");
     * if(BTSLUtil.isNullString(extranetUserId))
     * extranetUserId=request.getHeader("VFEUID");
     * 
     * if(_log.isDebugEnabled())_log.debug("doPost","extranetSessionId: "+
     * extranetSessionId+",, extranetUserId: "+extranetUserId);
     * if(BTSLUtil.isNullString(extranetSessionId) &&
     * BTSLUtil.isNullString(extranetUserId))
     * {
     * _log.error("doPost","VFESID not received from Extranet.");
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .RAISED,EventLevelI.FATAL,"ExtranetReceiver[doPost]","","","",
     * "VFESID or VFESID not received from Extranet");
     * throw new BTSLBaseException("VFESID not received from Extranet");
     * }
     * if(!BTSLUtil.isNullString(extranetSessionId) &&
     * BTSLUtil.isNullString(extranetUserId))
     * {
     * if(_log.isDebugEnabled())_log.debug("doPost","INSIDE 11111"+extraNetURL);
     * 
     * //URL url=new URL(extraNetURL);
     * URL url=new
     * URL(extraNetURL+"VFESID="+extranetSessionId+"&VFESYSID="+extraNetPretupsId
     * );
     * con= (HttpURLConnection)url.openConnection();
     * con.setReadTimeout(100000);
     * con.setConnectTimeout(100000);
     * con.setDoOutput(true);
     * con.setDoInput(true);
     * con.setRequestMethod("GET");
     * con.setRequestProperty("Content-Type","text/xml");
     * //con.addRequestProperty("VFESID",extranetSessionId);
     * //con.addRequestProperty("VFESYSID",extraNetPretupsId);
     * con.setUseCaches(false);
     * con.connect();
     * BufferedWriter wr = new BufferedWriter(new
     * OutputStreamWriter(con.getOutputStream(), "UTF8"));
     * // Send data
     * wr.write("");
     * wr.flush();
     * 
     * // Get response
     * InputStream rd = con.getInputStream();
     * int c = 0;
     * String line = "";
     * 
     * while ((c = rd.read()) != -1)
     * {
     * // Process line...
     * line += (char) c;
     * }
     * System.out.println("HTTP Message :"+con.getResponseMessage());
     * System.out.println("****** RESPONSE DATA ***************** \n"+line);
     * System.out.println(line);
     * 
     * wr.close();
     * rd.close();
     * 
     * System.out.println("Connected./.......................");
     * HashMap map =BTSLUtil.getStringToHash(line,"&","=");
     * extranetlangId=(String) map.get("VFELANGID");
     * //if(BTSLUtil.isNullString(extranetlangId))
     * extranetlangId="1";
     * 
     * extranetUserId=(String) map.get("VFEUID");
     * System.out.println("input " +extranetlangId+extranetUserId);
     * request.getSession(false).setAttribute("vfeUser", extranetUserId);
     * request.getSession(false).setAttribute("vfeLang", extranetlangId);
     * response.sendRedirect("/pretups/login.do?method=loadUserDetails&vfeUser="+
     * extranetUserId+"&vfeLang="+extranetlangId);
     * if(_log.isDebugEnabled())_log.debug("doPost",
     * "Redirecting the request..........");
     * 
     * //response.sendRedirect(response.encodeRedirectUrl(
     * "http://172.30.37.34:5555/pretups"
     * +"/login.do?method=loadUserDetails&vfeUser=manisha&vfeLang=1"));
     * //response.sendRedirect(response.encodeRedirectUrl("/pretups"+
     * "/login.do?method=loadUserDetails&vfeUser=manisha&vfeLang=1"));
     * if(_log.isDebugEnabled())_log.debug("doPost"," Request RedirectED.........."
     * );
     * 
     * //request.getSession(false).setAttribute("vfeUser",extranetUserId);
     * //request.getSession(false).setAttribute("vfeLang",extranetlangId);
     * //request.getRequestDispatcher("/login.do?method=loadUserDetails").forward
     * (request,response);
     * 
     * 
     * }
     * //else if((BTSLUtil.isNullString(extranetSessionId) &&
     * !BTSLUtil.isNullString(extranetUserId)))
     * else if((!BTSLUtil.isNullString(extranetSessionId)) &&
     * (!BTSLUtil.isNullString(extranetUserId)))
     * {
     * if(_log.isDebugEnabled())_log.debug("doPost","INSIDE 22222");
     * extranetlangId=request.getParameter("VFELANGID");
     * if(BTSLUtil.isNullString(extranetlangId))
     * extranetlangId=request.getHeader("VFELANGID");
     * if(BTSLUtil.isNullString(extranetlangId))
     * extranetlangId="1";
     * request.getSession(false).setAttribute("vfeUser",extranetUserId);
     * request.getSession(false).setAttribute("vfeLang",extranetlangId);
     * request.getRequestDispatcher("/login.do?method=loadUserDetails").forward(
     * request,response);
     * 
     * }
     * }
     * catch (Exception e)
     * {
     * e.printStackTrace();
     * _log.error("doPost", "Exception " + e.getMessage());
     * }
     * finally
     * {
     * if(_log.isDebugEnabled())_log.debug("doPost","Exited");
     * if(con != null)
     * con.disconnect();
     * }
     * }
     */
    public void destroy() {
        super.destroy();
    }

}
